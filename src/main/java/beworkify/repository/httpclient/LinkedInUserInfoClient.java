
package beworkify.repository.httpclient;

import beworkify.dto.response.LinkedInUserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "linkedin-userinfo-client", url = "https://api.linkedin.com")
public interface LinkedInUserInfoClient {
	@GetMapping("/v2/userinfo")
	LinkedInUserInfoResponse getUserInfo(@RequestHeader("Authorization") String authorization);
}
