package com.skipcart.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skipcart.domain.LoginRequest;
import com.skipcart.domain.PermissionRequest;
import com.skipcart.dto.*;
import com.skipcart.utils.JwtUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
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
    LoginRequest loginRequest = new LoginRequest(username, password);
    return makePostRequest(loginRequest);
  }

  public TokenResponse makePostRequest(LoginRequest request) {
    try{
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
          LoginResponse response = objMapper.readValue(responseBody, LoginResponse.class);
          User user = new User();
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
            List<GrantedAuthority> authorityList = new ArrayList<>();
            permissionResponse
                    .getResult()
                    .forEach(result -> authorityList.add((GrantedAuthority) result::getPermissionCode));
//            SkipCartUserDetail authenticationToken =
//                    new SkipCartUserDetail(authorityList, appToken, user.getUserToken(), user.getId());
//            SecurityContext context = SecurityContextHolder.createEmptyContext();
//            Assert.notNull(authenticationToken, "Authentication Token cannot be null");
//            context.setAuthentication(authenticationToken);
//            SecurityContextHolder.setContext(context);
            var scopes =
                    authorityList.stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(","));
            var jwtToken = jwtUtil.generateToken(user, scopes);
            return new TokenResponse(jwtToken, "Bearer", "will-be-provided-later");
          } else {
            log.error("Unable to fetch permissions in UserPermissionFilter");
            throw new RuntimeException("Not Authorized");
          }
      } else {
        throw new RuntimeException("Not Authorized");
      }
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }
}
