package beworkify.repository.httpclient;

import beworkify.dto.response.GoogleUserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "google-userinfo-client", url = "https://www.googleapis.com")
public interface GoogleUserInfoClient {
  @GetMapping("/oauth2/v1/userinfo?alt=json")
  GoogleUserInfoResponse getUserInfo(@RequestHeader("Authorization") String authorization);
}
