
package beworkify.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

import beworkify.dto.request.JobBenefitRequest;
import beworkify.dto.request.JobRequest;
import beworkify.dto.request.LocationRequest;
import beworkify.dto.response.JobResponse;
import beworkify.dto.response.ResponseData;
import beworkify.service.JobService;

@ExtendWith(MockitoExtension.class)
public class JobControllerUnitTest {

	@Mock
	private JobService jobService;

	@Mock
	private MessageSource messageSource;

	@InjectMocks
	private JobController jobController;

	private Map<String, Object> testData;
	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
    void setUp() throws IOException {
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Success");

        // Load test data from JSON
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("testdata/job-testdata.json");
        testData = objectMapper.readValue(inputStream, Map.class);
    }

	private JobRequest createValidJobRequest() {
		Map<String, Object> validData = (Map<String, Object>) testData.get("validJobRequest");

		JobRequest request = new JobRequest();
		request.setCompanyName((String) validData.get("companyName"));
		request.setCompanySize((String) validData.get("companySize"));
		request.setAboutCompany((String) validData.get("aboutCompany"));
		request.setJobTitle((String) validData.get("jobTitle"));
		request.setJobDescription((String) validData.get("jobDescription"));
		request.setRequirement((String) validData.get("requirement"));
		request.setJobLevel((String) validData.get("jobLevel"));
		request.setJobType((String) validData.get("jobType"));
		request.setMinSalary(((Number) validData.get("minSalary")).doubleValue());
		request.setMaxSalary(((Number) validData.get("maxSalary")).doubleValue());
		request.setExpirationDate(LocalDate.now().plusDays(30));
		request.setContactPerson((String) validData.get("contactPerson"));
		request.setPhoneNumber((String) validData.get("phoneNumber"));

		Map<String, Object> jobLocationData = (Map<String, Object>) validData.get("jobLocation");
		LocationRequest jobLocation = new LocationRequest();
		jobLocation.setProvinceId(((Number) jobLocationData.get("provinceId")).longValue());
		jobLocation.setDistrictId(((Number) jobLocationData.get("districtId")).longValue());
		request.setJobLocations(Arrays.asList(jobLocation));

		Map<String, Object> contactLocationData = (Map<String, Object>) validData.get("contactLocation");
		LocationRequest contactLocation = new LocationRequest();
		contactLocation.setProvinceId(((Number) contactLocationData.get("provinceId")).longValue());
		contactLocation.setDistrictId(((Number) contactLocationData.get("districtId")).longValue());
		request.setContactLocation(contactLocation);

		Map<String, Object> benefitData = (Map<String, Object>) validData.get("jobBenefit");
		JobBenefitRequest benefit = JobBenefitRequest.builder().type((String) benefitData.get("type"))
				.description((String) benefitData.get("description")).build();
		request.setJobBenefits(Arrays.asList(benefit));

		List<Integer> industryIdsInt = (List<Integer>) validData.get("industryIds");
		request.setIndustryIds(industryIdsInt.stream().map(Integer::longValue).toList());

		return request;
	}

	@Test
	void testCreateJob_Success() {
		// Given
		JobRequest request = createValidJobRequest();

		JobResponse mockResponse = JobResponse.builder().jobTitle(request.getJobTitle())
				.companyName(request.getCompanyName()).minSalary(request.getMinSalary())
				.maxSalary(request.getMaxSalary()).build();

		when(jobService.create(any(JobRequest.class))).thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<JobResponse>> response = jobController.create(request);

		// Then
		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals(201, response.getBody().getStatus());
		verify(jobService, times(1)).create(any(JobRequest.class));
	}

	@Test
	void testCreateJob_EntryLevel() {
		// Given - Load from JSON
		JobRequest request = createValidJobRequest();
		Map<String, Object> levelData = (Map<String, Object>) testData.get("entryLevelJob");
		request.setJobLevel((String) levelData.get("jobLevel"));
		request.setJobTitle((String) levelData.get("jobTitle"));

		JobResponse mockResponse = JobResponse.builder().jobTitle((String) levelData.get("jobTitle")).build();

		when(jobService.create(argThat(req -> "ENTRY_LEVEL".equals(req.getJobLevel())))).thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<JobResponse>> response = jobController.create(request);

		// Then
		assertNotNull(response);
		verify(jobService, times(1)).create(argThat(req -> "ENTRY_LEVEL".equals(req.getJobLevel())));
	}

	@Test
	void testCreateJob_StaffLevel() {
		// Given - Load from JSON
		JobRequest request = createValidJobRequest();
		Map<String, Object> levelData = (Map<String, Object>) testData.get("staffLevelJob");
		request.setJobLevel((String) levelData.get("jobLevel"));

		JobResponse mockResponse = JobResponse.builder().jobTitle((String) levelData.get("jobTitle")).build();

		when(jobService.create(argThat(req -> "STAFF".equals(req.getJobLevel())))).thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<JobResponse>> response = jobController.create(request);

		// Then
		assertNotNull(response);
		verify(jobService, times(1)).create(argThat(req -> "STAFF".equals(req.getJobLevel())));
	}

	@Test
	void testCreateJob_ManagerLevel() {
		// Given - Load from JSON
		JobRequest request = createValidJobRequest();
		Map<String, Object> levelData = (Map<String, Object>) testData.get("managerLevelJob");
		request.setJobLevel((String) levelData.get("jobLevel"));

		JobResponse mockResponse = JobResponse.builder().jobTitle((String) levelData.get("jobTitle")).build();

		when(jobService.create(argThat(req -> "MANAGER".equals(req.getJobLevel())))).thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<JobResponse>> response = jobController.create(request);

		// Then
		assertNotNull(response);
		verify(jobService, times(1)).create(argThat(req -> "MANAGER".equals(req.getJobLevel())));
	}

	@Test
	void testCreateJob_FullTimeType() {
		// Given - Load from JSON
		JobRequest request = createValidJobRequest();
		Map<String, Object> typeData = (Map<String, Object>) testData.get("fullTimeJob");
		request.setJobType((String) typeData.get("jobType"));

		JobResponse mockResponse = JobResponse.builder().jobTitle((String) typeData.get("jobTitle")).build();

		when(jobService.create(argThat(req -> "FULL_TIME".equals(req.getJobType())))).thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<JobResponse>> response = jobController.create(request);

		// Then
		assertNotNull(response);
		verify(jobService, times(1)).create(argThat(req -> "FULL_TIME".equals(req.getJobType())));
	}

	@Test
	void testCreateJob_PartTimeType() {
		// Given - Load from JSON
		JobRequest request = createValidJobRequest();
		Map<String, Object> typeData = (Map<String, Object>) testData.get("partTimeJob");
		request.setJobType((String) typeData.get("jobType"));

		JobResponse mockResponse = JobResponse.builder().jobTitle((String) typeData.get("jobTitle")).build();

		when(jobService.create(argThat(req -> "PART_TIME".equals(req.getJobType())))).thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<JobResponse>> response = jobController.create(request);

		// Then
		assertNotNull(response);
		verify(jobService, times(1)).create(argThat(req -> "PART_TIME".equals(req.getJobType())));
	}

	@Test
	void testCreateJob_ContractType() {
		// Given - Load from JSON
		JobRequest request = createValidJobRequest();
		Map<String, Object> typeData = (Map<String, Object>) testData.get("contractJob");
		request.setJobType((String) typeData.get("jobType"));

		JobResponse mockResponse = JobResponse.builder().jobTitle((String) typeData.get("jobTitle")).build();

		when(jobService.create(argThat(req -> "CONTRACT".equals(req.getJobType())))).thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<JobResponse>> response = jobController.create(request);

		// Then
		assertNotNull(response);
		verify(jobService, times(1)).create(argThat(req -> "CONTRACT".equals(req.getJobType())));
	}

	@Test
	void testCreateJob_InternshipType() {
		// Given - Load from JSON
		JobRequest request = createValidJobRequest();
		Map<String, Object> typeData = (Map<String, Object>) testData.get("internshipJob");
		request.setJobType((String) typeData.get("jobType"));

		JobResponse mockResponse = JobResponse.builder().jobTitle((String) typeData.get("jobTitle")).build();

		when(jobService.create(argThat(req -> "INTERNSHIP".equals(req.getJobType())))).thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<JobResponse>> response = jobController.create(request);

		// Then
		assertNotNull(response);
		verify(jobService, times(1)).create(argThat(req -> "INTERNSHIP".equals(req.getJobType())));
	}

	@Test
	void testCreateJob_ValidSalaryRange() {
		// Given - Load from JSON
		JobRequest request = createValidJobRequest();
		Map<String, Object> salaryData = (Map<String, Object>) testData.get("salaryRange");
		request.setMinSalary(((Number) salaryData.get("minSalary")).doubleValue());
		request.setMaxSalary(((Number) salaryData.get("maxSalary")).doubleValue());

		JobResponse mockResponse = JobResponse.builder().minSalary(request.getMinSalary())
				.maxSalary(request.getMaxSalary()).build();

		when(jobService
				.create(argThat(req -> req.getMinSalary().equals(15000000.0) && req.getMaxSalary().equals(25000000.0))))
				.thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<JobResponse>> response = jobController.create(request);

		// Then
		assertNotNull(response);
		verify(jobService, times(1))
				.create(argThat(req -> req.getMinSalary().equals(15000000.0) && req.getMaxSalary().equals(25000000.0)));
	}

	@Test
	void testCreateJob_FutureExpiryDate() {
		// Given - Load from JSON
		JobRequest request = createValidJobRequest();
		int daysInFuture = (Integer) testData.get("futureExpiryDays");
		LocalDate futureDate = LocalDate.now().plusDays(daysInFuture);
		request.setExpirationDate(futureDate);

		JobResponse mockResponse = JobResponse.builder().jobTitle("Job with Future Expiry").build();

		when(jobService.create(argThat(req -> req.getExpirationDate().isAfter(LocalDate.now()))))
				.thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<JobResponse>> response = jobController.create(request);

		// Then
		assertNotNull(response);
		verify(jobService, times(1)).create(argThat(req -> req.getExpirationDate().isAfter(LocalDate.now())));
	}

	@Test
	void testCreateJob_WithLocation() {
		// Given - Load from JSON
		JobRequest request = createValidJobRequest();
		Map<String, Object> locationData = (Map<String, Object>) testData.get("alternativeLocation");
		LocationRequest location = new LocationRequest();
		location.setProvinceId(((Number) locationData.get("provinceId")).longValue());
		location.setDistrictId(((Number) locationData.get("districtId")).longValue());
		request.setJobLocations(Arrays.asList(location));

		JobResponse mockResponse = JobResponse.builder().jobTitle("Job with Location").build();

		when(jobService.create(argThat(req -> req.getJobLocations() != null && !req.getJobLocations().isEmpty()
				&& req.getJobLocations().get(0).getProvinceId().equals(2L)))).thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<JobResponse>> response = jobController.create(request);

		// Then
		assertNotNull(response);
		verify(jobService, times(1)).create(argThat(req -> req.getJobLocations() != null
				&& !req.getJobLocations().isEmpty() && req.getJobLocations().get(0).getProvinceId().equals(2L)));
	}

	@Test
	void testCreateJob_WithBenefits() {
		// Given - Load from JSON
		JobRequest request = createValidJobRequest();
		List<Map<String, Object>> benefitsData = (List<Map<String, Object>>) testData.get("multipleBenefits");

		List<JobBenefitRequest> benefits = benefitsData.stream().map(b -> JobBenefitRequest.builder()
				.type((String) b.get("type")).description((String) b.get("description")).build()).toList();
		request.setJobBenefits(benefits);

		JobResponse mockResponse = JobResponse.builder().jobTitle("Job with Benefits").build();

		when(jobService.create(argThat(req -> req.getJobBenefits() != null && !req.getJobBenefits().isEmpty())))
				.thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<JobResponse>> response = jobController.create(request);

		// Then
		assertNotNull(response);
		verify(jobService, times(1))
				.create(argThat(req -> req.getJobBenefits() != null && !req.getJobBenefits().isEmpty()));
	}

	@Test
	void testCreateJob_NoBenefits() {
		// Given
		JobRequest request = createValidJobRequest();
		request.setJobBenefits(Collections.emptyList());

		JobResponse mockResponse = JobResponse.builder().jobTitle("Job without Benefits").build();

		when(jobService.create(argThat(req -> req.getJobBenefits() != null && req.getJobBenefits().isEmpty())))
				.thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<JobResponse>> response = jobController.create(request);

		// Then
		assertNotNull(response);
		verify(jobService, times(1))
				.create(argThat(req -> req.getJobBenefits() != null && req.getJobBenefits().isEmpty()));
	}

	@Test
	void testCreateJob_LongTitle() {
		// Given - Load from JSON
		JobRequest request = createValidJobRequest();
		String longTitle = (String) testData.get("longJobTitle");
		request.setJobTitle(longTitle);

		JobResponse mockResponse = JobResponse.builder().jobTitle(longTitle).build();

		when(jobService.create(argThat(req -> req.getJobTitle().equals(longTitle)))).thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<JobResponse>> response = jobController.create(request);

		// Then
		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals(longTitle, response.getBody().getData().getJobTitle());
		verify(jobService, times(1)).create(argThat(req -> req.getJobTitle().equals(longTitle)));
	}

	@Test
	void testCreateJob_DetailedDescription() {
		// Given - Load from JSON
		JobRequest request = createValidJobRequest();
		String detailedDesc = (String) testData.get("detailedDescription");
		request.setJobDescription(detailedDesc);

		JobResponse mockResponse = JobResponse.builder().jobTitle("Senior Java Developer").build();

		when(jobService.create(argThat(req -> req.getJobDescription().contains("Senior Java Developer"))))
				.thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<JobResponse>> response = jobController.create(request);

		// Then
		assertNotNull(response);
		verify(jobService, times(1)).create(argThat(req -> req.getJobDescription().contains("Senior Java Developer")));
	}

	@Test
	void testCreateJob_ServiceCalledOnce() {
		// Given
		JobRequest request = createValidJobRequest();

		JobResponse mockResponse = JobResponse.builder().jobTitle("Test Job").build();

		when(jobService.create(any(JobRequest.class))).thenReturn(mockResponse);

		// When
		jobController.create(request);

		// Then
		verify(jobService, times(1)).create(any(JobRequest.class));
	}

	@Test
	void testCreateJob_ResponseNotNull() {
		// Given
		JobRequest request = createValidJobRequest();

		JobResponse mockResponse = JobResponse.builder().jobTitle("Test Job").companyName("Test Company").build();

		when(jobService.create(any(JobRequest.class))).thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<JobResponse>> response = jobController.create(request);

		// Then
		assertNotNull(response);
		assertNotNull(response.getBody());
		assertNotNull(response.getBody().getData());
		assertNotNull(response.getBody().getData().getJobTitle());
		assertNotNull(response.getBody().getData().getCompanyName());
	}

	@Test
	void testCreateJob_WithIndustryIds() {
		// Given - Load from JSON
		JobRequest request = createValidJobRequest();
		List<Integer> industryIdsInt = (List<Integer>) testData.get("multipleIndustryIds");
		List<Long> industryIds = industryIdsInt.stream().map(Integer::longValue).toList();
		request.setIndustryIds(industryIds);

		JobResponse mockResponse = JobResponse.builder().jobTitle("Multi-Industry Job").build();

		when(jobService.create(argThat(req -> req.getIndustryIds() != null && req.getIndustryIds().size() == 3
				&& req.getIndustryIds().contains(1L)))).thenReturn(mockResponse);

		// When
		ResponseEntity<ResponseData<JobResponse>> response = jobController.create(request);

		// Then
		assertNotNull(response);
		verify(jobService, times(1)).create(argThat(req -> req.getIndustryIds() != null
				&& req.getIndustryIds().size() == 3 && req.getIndustryIds().contains(1L)));
	}
}
