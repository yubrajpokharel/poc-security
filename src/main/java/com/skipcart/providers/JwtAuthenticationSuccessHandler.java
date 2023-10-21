package com.skipcart.providers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * @author Anish Panthi
 */
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
  /**
   * @param request - HttpServletRequest
   * @param response - HttpServletResponse
   * @param authentication - Authentication
   * @throws IOException - IOException
   * @throws ServletException - ServletException
   */
  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    // We do not need to do anything extra on REST authentication success, because there is no page
    // to redirect to
  }
}
