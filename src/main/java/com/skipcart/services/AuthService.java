package com.skipcart.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skipcart.domain.LoginRequest;
import com.skipcart.dto.LoginResponse;
import com.skipcart.dto.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AuthService{

  @Value("${external.api.url}")
  private String coreApiUrl;

  @Value("${app.token}")
  private String appToken;

  private final RestTemplate restTemplate;
  private final ObjectMapper mapper;

  private final BCryptPasswordEncoder passwordEncoder;

  private final AuthenticationManager authenticationManager;

  public AuthService(RestTemplate restTemplate, ObjectMapper mapper,
      BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
    this.restTemplate = restTemplate;
    this.mapper = mapper;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
  }

  public User authenticate(String username, String password) {

    LoginRequest loginRequest = new LoginRequest(username, password);
    User user = makePostRequest(loginRequest);

    Authentication authenticationToken = new UsernamePasswordAuthenticationToken(username, passwordEncoder.encode(password));
    Authentication authentication = authenticationManager.authenticate(authenticationToken);
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    return user;

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

    if (responseEntity.getStatusCode().is2xxSuccessful()) {
      String responseBody = responseEntity.getBody();
      try {
        ObjectMapper objMapper = new ObjectMapper();
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
