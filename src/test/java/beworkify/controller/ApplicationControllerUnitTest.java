
package beworkify.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import beworkify.dto.request.ApplicationRequest;
import beworkify.dto.response.ApplicationResponse;
import beworkify.dto.response.ResponseData;
import beworkify.service.ApplicationService;
import jakarta.validation.Validator;

@ExtendWith(MockitoExtension.class)
public class ApplicationControllerUnitTest {

	@Mock
	private ApplicationService applicationService;

	@Mock
	private MessageSource messageSource;

	@Mock
	private Validator validator;

	@InjectMocks
	private ApplicationController applicationController;

	private Map<String, Map<String, Object>> testData;
	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
    void setUp() throws IOException {
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Success");

        // Load test data from JSON
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("testdata/application-testdata.json");
        testData = objectMapper.readValue(inputStream, Map.class);
    }

	private ApplicationRequest createRequestFromJson(Map<String, Object> data) {
		ApplicationRequest request = new ApplicationRequest();
		request.setFullName((String) data.get("fullName"));
		request.setEmail((String) data.get("email"));
		request.setPhoneNumber((String) data.get("phoneNumber"));
		request.setCoverLetter((String) data.get("coverLetter"));
		request.setJobId(((Number) data.get("jobId")).longValue());
		return request;
	}

	private MultipartFile createMockFile(Map<String, Object> data) {
		String fileName = (String) data.get("cvFileName");
		int fileSize = ((Number) data.get("cvFileSize")).intValue();
		String contentType = (String) data.get("cvContentType");
		byte[] content = new byte[fileSize];
		return new MockMultipartFile("cv", fileName, contentType, content);
	}

	@Test
	void testApplyJob_WithPdfCv() {
		// Given - Load data from JSON
		Map<String, Object> data = testData.get("validApplication");
		ApplicationRequest request = createRequestFromJson(data);
		MultipartFile cv = createMockFile(data);

		ApplicationResponse mockResponse = ApplicationResponse.builder().fullName((String) data.get("fullName"))
				.email((String) data.get("email")).build();

		when(applicationService.create(any(ApplicationRequest.class), any(MultipartFile.class)))
				.thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<ApplicationResponse>> response = applicationController.apply(request, cv);

		// Then
		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals(201, response.getBody().getStatus());
		verify(applicationService, times(1)).create(any(ApplicationRequest.class), any(MultipartFile.class));
	}

	@Test
	void testApplyJob_WithDocxCv() {
		// Given - Load data from JSON
		Map<String, Object> data = testData.get("validApplicationDocx");
		ApplicationRequest request = createRequestFromJson(data);
		MultipartFile cv = createMockFile(data);

		ApplicationResponse mockResponse = ApplicationResponse.builder().fullName((String) data.get("fullName"))
				.email((String) data.get("email")).build();

		when(applicationService.create(any(ApplicationRequest.class), any(MultipartFile.class)))
				.thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<ApplicationResponse>> response = applicationController.apply(request, cv);

		// Then
		assertNotNull(response);
		verify(applicationService, times(1)).create(any(ApplicationRequest.class), any(MultipartFile.class));
	}

	@Test
	void testApplyJob_WithCoverLetter() {
		// Given - Load data from JSON
		Map<String, Object> data = testData.get("validApplication");
		ApplicationRequest request = createRequestFromJson(data);
		MultipartFile cv = createMockFile(data);

		ApplicationResponse mockResponse = ApplicationResponse.builder().coverLetter((String) data.get("coverLetter"))
				.build();

		when(applicationService.create(argThat(req -> req.getCoverLetter() != null && !req.getCoverLetter().isEmpty()),
				any(MultipartFile.class))).thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<ApplicationResponse>> response = applicationController.apply(request, cv);

		// Then
		assertNotNull(response);
		verify(applicationService, times(1)).create(
				argThat(req -> req.getCoverLetter() != null && !req.getCoverLetter().isEmpty()),
				any(MultipartFile.class));
	}

	@Test
	void testApplyJob_ValidEmail() {
		// Given - Load data from JSON
		Map<String, Object> data = testData.get("validApplication");
		ApplicationRequest request = createRequestFromJson(data);
		MultipartFile cv = createMockFile(data);

		ApplicationResponse mockResponse = ApplicationResponse.builder().email((String) data.get("email")).build();

		when(applicationService.create(argThat(req -> req.getEmail().contains("@")), any(MultipartFile.class)))
				.thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<ApplicationResponse>> response = applicationController.apply(request, cv);

		// Then
		assertNotNull(response);
		verify(applicationService, times(1)).create(argThat(req -> req.getEmail().contains("@")),
				any(MultipartFile.class));
	}

	@Test
	void testApplyJob_ValidPhoneNumber() {
		// Given - Load data from JSON
		Map<String, Object> data = testData.get("validApplication");
		ApplicationRequest request = createRequestFromJson(data);
		MultipartFile cv = createMockFile(data);

		ApplicationResponse mockResponse = ApplicationResponse.builder().phoneNumber((String) data.get("phoneNumber"))
				.build();

		when(applicationService.create(
				argThat(req -> req.getPhoneNumber() != null && req.getPhoneNumber().startsWith("0")),
				any(MultipartFile.class))).thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<ApplicationResponse>> response = applicationController.apply(request, cv);

		// Then
		assertNotNull(response);
		verify(applicationService, times(1)).create(
				argThat(req -> req.getPhoneNumber() != null && req.getPhoneNumber().startsWith("0")),
				any(MultipartFile.class));
	}

	@Test
	void testApplyJob_ValidFullName() {
		// Given - Load data from JSON
		Map<String, Object> data = testData.get("validApplication");
		ApplicationRequest request = createRequestFromJson(data);
		MultipartFile cv = createMockFile(data);

		ApplicationResponse mockResponse = ApplicationResponse.builder().fullName((String) data.get("fullName"))
				.build();

		when(applicationService.create(argThat(req -> req.getFullName() != null && !req.getFullName().isEmpty()),
				any(MultipartFile.class))).thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<ApplicationResponse>> response = applicationController.apply(request, cv);

		// Then
		assertNotNull(response);
		verify(applicationService, times(1)).create(
				argThat(req -> req.getFullName() != null && !req.getFullName().isEmpty()), any(MultipartFile.class));
	}

	@Test
	void testApplyJob_LongCoverLetter() {
		// Given - Load data from JSON
		Map<String, Object> data = testData.get("longCoverLetter");
		ApplicationRequest request = createRequestFromJson(data);
		MultipartFile cv = createMockFile(testData.get("validApplication"));

		ApplicationResponse mockResponse = ApplicationResponse.builder().coverLetter((String) data.get("coverLetter"))
				.build();

		when(applicationService.create(argThat(req -> req.getCoverLetter().length() > 100), any(MultipartFile.class)))
				.thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<ApplicationResponse>> response = applicationController.apply(request, cv);

		// Then
		assertNotNull(response);
		verify(applicationService, times(1)).create(argThat(req -> req.getCoverLetter().length() > 100),
				any(MultipartFile.class));
	}

	@Test
	void testApplyJob_ValidJobId() {
		// Given - Load data from JSON
		Map<String, Object> data = testData.get("validJobId");
		ApplicationRequest request = createRequestFromJson(data);
		MultipartFile cv = createMockFile(testData.get("validApplication"));

		ApplicationResponse mockResponse = ApplicationResponse.builder().fullName((String) data.get("fullName"))
				.email((String) data.get("email")).build();

		when(applicationService.create(argThat(req -> req.getJobId() != null && req.getJobId() > 0),
				any(MultipartFile.class))).thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<ApplicationResponse>> response = applicationController.apply(request, cv);

		// Then
		assertNotNull(response);
		verify(applicationService, times(1)).create(argThat(req -> req.getJobId() != null && req.getJobId() > 0),
				any(MultipartFile.class));
	}

	@Test
	void testApplyJob_ServiceCalledOnce() {
		// Given - Load data from JSON
		Map<String, Object> data = testData.get("validApplication");
		ApplicationRequest request = createRequestFromJson(data);
		MultipartFile cv = createMockFile(data);

		ApplicationResponse mockResponse = ApplicationResponse.builder().fullName("Test").build();

		when(applicationService.create(any(ApplicationRequest.class), any(MultipartFile.class)))
				.thenReturn(mockResponse);

		// When
		applicationController.apply(request, cv);

		// Then
		verify(applicationService, times(1)).create(any(ApplicationRequest.class), any(MultipartFile.class));
	}
}
