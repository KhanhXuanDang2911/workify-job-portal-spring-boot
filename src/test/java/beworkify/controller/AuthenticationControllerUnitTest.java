
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

import com.fasterxml.jackson.databind.ObjectMapper;

import beworkify.dto.request.SignInRequest;
import beworkify.dto.response.TokenResponse;
import beworkify.dto.response.UserResponse;
import beworkify.service.AuthenticationService;
import beworkify.service.EmployerService;
import beworkify.service.UserService;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerUnitTest {

	@Mock
	private AuthenticationService authenticationService;

	@Mock
	private UserService userService;

	@Mock
	private EmployerService employerService;

	@Mock
	private MessageSource messageSource;

	@InjectMocks
	private AuthenticationController authenticationController;

	private Map<String, Map<String, Object>> testData;
	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
    void setUp() throws IOException {
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Success");

        // Load test data from JSON
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("testdata/auth-signin-testdata.json");
        testData = objectMapper.readValue(inputStream, Map.class);
    }

	@SuppressWarnings("unchecked")
	@Test
	void testUserSignIn_Success() throws IOException {
		// Given - Load data from JSON
		Map<String, Object> validData = testData.get("validSignIn");

		SignInRequest request = new SignInRequest();
		request.setEmail((String) validData.get("email"));
		request.setPassword((String) validData.get("password"));

		UserResponse userResponse = UserResponse.builder().id(((Number) validData.get("expectedUserId")).longValue())
				.email((String) validData.get("expectedEmail")).build();

		TokenResponse<UserResponse> tokenResponse = TokenResponse.<UserResponse>builder()
				.accessToken("mock-access-token").refreshToken("mock-refresh-token").data(userResponse).build();

		when(authenticationService.userSignIn(any(SignInRequest.class))).thenReturn(tokenResponse);

		// When
		authenticationController.userSignIn(request);

		// Then
		verify(authenticationService, times(1)).userSignIn(any(SignInRequest.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testUserSignIn_ServiceCalled() throws IOException {
		// Given - Load data from JSON
		Map<String, Object> validData = testData.get("validSignIn");

		SignInRequest request = new SignInRequest();
		request.setEmail((String) validData.get("email"));
		request.setPassword((String) validData.get("password"));

		UserResponse userResponse = UserResponse.builder().id(1L).email((String) validData.get("expectedEmail"))
				.build();

		TokenResponse<UserResponse> tokenResponse = TokenResponse.<UserResponse>builder().accessToken("mock-token")
				.data(userResponse).build();

		when(authenticationService.userSignIn(any(SignInRequest.class))).thenReturn(tokenResponse);

		// When
		authenticationController.userSignIn(request);

		// Then
		verify(authenticationService, times(1)).userSignIn(any(SignInRequest.class));
	}
}
