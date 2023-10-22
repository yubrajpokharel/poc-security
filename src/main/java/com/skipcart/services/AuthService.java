package com.skipcart.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skipcart.domain.LoginRequest;
import com.skipcart.domain.PermissionRequest;
import com.skipcart.dto.*;
import com.skipcart.utils.JwtUtil;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

  @Value("${external.api.url}")
  private String coreApiUrl;

  @Value("${app.token}")
  private String appToken;

  private final JwtUtil jwtUtil;

  private final RestTemplate restTemplate;

  public TokenResponse authenticate(String username, String password, String appToken) {
    if (!appToken.equals(this.appToken)) {
      throw new RuntimeException("Not Authorized: Invalid App Token");
    }
    var loginRequest = new LoginRequest(username, password);
    return makePostRequest(loginRequest);
  }

  public TokenResponse makePostRequest(LoginRequest request) {
    try {
      // Create a HttpHeaders object with the appropriate content type
      var headers = new HttpHeaders();
      headers.set("Content-Type", "application/json");
      headers.set("AppToken", appToken);

      // Create an HttpEntity with the LoginRequest and headers
      var requestEntity = new HttpEntity<>(request, headers);

      // Make the POST request
      var responseEntity =
          restTemplate.exchange(coreApiUrl, HttpMethod.POST, requestEntity, String.class);
      var objMapper = new ObjectMapper();

      if (responseEntity.getStatusCode().is2xxSuccessful()) {
        var responseBody = responseEntity.getBody();
        var response = objMapper.readValue(responseBody, LoginResponse.class);
        var user = new User();
        user.setId(response.getResult().getId());
        user.setUsername(response.getResult().getFullName());
        user.setEmail(response.getResult().getEmail());
        user.setUserToken(response.getResult().getUserToken());
        user.setUserTokenExpires(response.getResult().getUserTokenExpires());

        var permissionHeaders = new HttpHeaders();
        permissionHeaders.set("Content-Type", "application/json");
        permissionHeaders.set("appToken", appToken);
        permissionHeaders.set("userToken", user.getUserToken());

        // Create Request Entity
        var permissionRequest = new PermissionRequest(user.getId(), "login");
        var permissionRequestEntity = new HttpEntity<>(permissionRequest, permissionHeaders);

        // Make the POST request
        var permissionResponseString =
            new RestTemplate()
                .exchange(
                    "https://devapi.skipcart.com/v1api/Permission/UserPermission",
                    HttpMethod.POST,
                    permissionRequestEntity,
                    String.class);
        if (permissionResponseString.getStatusCode().is2xxSuccessful()) {
          var responseString = permissionResponseString.getBody();
          var permissionResponse =
              new ObjectMapper().readValue(responseString, PermissionResponse.class);
          var userScope =
              permissionResponse.getResult().stream()
                  .map(PermissionResponse.Result::getPermissionCode)
                  .collect(Collectors.joining(","));
          var jwtToken = jwtUtil.generateToken(user, userScope);
          return new TokenResponse(jwtToken, "Bearer", "will-be-provided-later");
        } else {
          log.error("Unable to fetch permissions in UserPermissionFilter");
          throw new RuntimeException("Not Authorized");
        }
      } else {
        throw new RuntimeException("Not Authorized");
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
