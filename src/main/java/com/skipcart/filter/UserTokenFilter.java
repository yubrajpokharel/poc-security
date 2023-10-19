package com.skipcart.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skipcart.domain.PermissionRequest;
import com.skipcart.dto.PermissionResponse;
import com.skipcart.dto.SkipCartUserDetail;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class UserTokenFilter extends OncePerRequestFilter {

  @Value("${app.token}")
  String APP_TOKEN;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    var requestUri = request.getRequestURI();
    try {
      if (requestUri.startsWith("/api")) {
        String appToken = request.getHeader("AppToken");
        if (appToken != null && appToken.equals(APP_TOKEN)) {
          filterChain.doFilter(request, response);
        } else {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
      } else if (requestUri.startsWith("/v1api")) {
        var appToken = request.getHeader("AppToken");
        var userToken = request.getHeader("UserToken");
        var userId = request.getHeader("UserId");
        var module = request.getHeader("Module");
        if (StringUtils.hasText(appToken)
            && StringUtils.hasText(userToken)
            && StringUtils.hasText(userId)
            && StringUtils.hasText(module)
            && appToken.equals(APP_TOKEN)) {

          // =================================== Retrieve User's Permission
          // Create Header
          var permissionHeaders = new HttpHeaders();
          permissionHeaders.set("Content-Type", "application/json");
          permissionHeaders.set("apptoken", appToken);
          permissionHeaders.set("usertoken", userToken);

          // Create Request Entity
          var permissionRequest = new PermissionRequest(userId, module);
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
            SkipCartUserDetail authenticationToken =
                new SkipCartUserDetail(authorityList, appToken, userToken, userId);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            Assert.notNull(authenticationToken, "Authentication Token cannot be null");
            context.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(context);
            log.debug("Authentication Token: {}", authenticationToken);
            filterChain.doFilter(request, response);
          } else {
            log.error("Unable to fetch permissions in UserPermissionFilter");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          }
        } else {
          log.error("Headers are missing in UserPermissionFilter");
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
      } else {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      }
    } catch (Exception e) {
      log.error("Error in UserTokenFilter", e);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
  }
}
