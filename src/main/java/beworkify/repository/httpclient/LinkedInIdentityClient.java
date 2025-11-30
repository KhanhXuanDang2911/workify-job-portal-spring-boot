package beworkify.repository.httpclient;

import beworkify.dto.response.ExchangeTokenLinkedInResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "linkedin-identity-client", url = "https://www.linkedin.com")
public interface LinkedInIdentityClient {

  @PostMapping(
      value = "/oauth/v2/accessToken",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  ExchangeTokenLinkedInResponse getToken(
      @RequestParam("grant_type") String grantType,
      @RequestParam("code") String code,
      @RequestParam("redirect_uri") String redirectUri,
      @RequestParam("client_id") String clientId,
      @RequestParam("client_secret") String clientSecret);
}
