package com.skipcart.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skipcart.domain.LoginRequest;
import com.skipcart.dto.LoginResponse;
import com.skipcart.dto.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AuthService {

  @Value("${external.api.url}")
  private String coreApiUrl;

  @Value("${app.token}")
  private String appToken;

  private final RestTemplate restTemplate;

  public AuthService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public User authenticate(String username, String password) {
    LoginRequest loginRequest = new LoginRequest(username, password);
    return makePostRequest(loginRequest);
  }

  public User makePostRequest(LoginRequest request) {

    // Create a HttpHeaders object with the appropriate content type
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    headers.set("AppToken", appToken);

    // Create an HttpEntity with the LoginRequest and headers
    HttpEntity<LoginRequest> requestEntity = new HttpEntity<>(request, headers);

    // Make the POST request
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(coreApiUrl, HttpMethod.POST, requestEntity, String.class);
    ObjectMapper objMapper = new ObjectMapper();

    if (responseEntity.getStatusCode().is2xxSuccessful()) {
      String responseBody = responseEntity.getBody();
      try {
        LoginResponse response = objMapper.readValue(responseBody, LoginResponse.class);
        User user = new User();
        user.setId(response.getResult().getId());
        user.setUsername(response.getResult().getFullName());
        user.setEmail(response.getResult().getEmail());
        user.setUserToken(response.getResult().getUserToken());
        user.setUserTokenExpires(response.getResult().getUserTokenExpires());
        return user;
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    } else {
      throw new RuntimeException("Not Authorized");
    }
  }
}
