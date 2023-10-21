package com.skipcart.filter;

import com.skipcart.dto.JwtAuthenticationToken;
import com.skipcart.exception.JwtTokenMissingException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

/**
 * @author Anish Panthi
 */
@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  public JwtAuthenticationFilter() {
    super("/v1api/**");
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      throw new JwtTokenMissingException("No JWT token found in request headers");
    }

    String authToken = header.substring(7);
    JwtAuthenticationToken authRequest = new JwtAuthenticationToken(authToken);
    return getAuthenticationManager().authenticate(authRequest);
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authResult)
      throws IOException, ServletException {
    super.successfulAuthentication(request, response, chain, authResult);
    chain.doFilter(request, response);
  }
}
