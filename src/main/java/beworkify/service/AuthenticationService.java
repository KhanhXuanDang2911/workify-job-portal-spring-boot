
package beworkify.service;

import beworkify.dto.request.SignInRequest;
import beworkify.dto.response.EmployerResponse;
import beworkify.dto.response.TokenResponse;
import beworkify.dto.response.UserResponse;

public interface AuthenticationService {
	TokenResponse<EmployerResponse> employerSignIn(SignInRequest request);

	TokenResponse<UserResponse> userSignIn(SignInRequest request);

	TokenResponse<UserResponse> authenticateGoogle(String code);

	TokenResponse<Void> refreshTokenUser(String refreshToken);

	TokenResponse<Void> refreshTokenEmployer(String refreshToken);

	void signOut(String accessToken, String refreshToken);

	TokenResponse<UserResponse> authenticateLinkedIn(String code);
}
