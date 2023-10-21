package com.skipcart.providers;

import com.skipcart.dto.AuthenticatedUser;
import com.skipcart.dto.JwtAuthenticationToken;
import com.skipcart.utils.JwtUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * @author Anish Panthi
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

  private final JwtUtil jwtUtil;

  @Override
  protected void additionalAuthenticationChecks(
      UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
      throws AuthenticationException {}

  @Override
  protected UserDetails retrieveUser(
      String username, UsernamePasswordAuthenticationToken authentication)
      throws AuthenticationException {
    try {
      JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
      String token = jwtAuthenticationToken.getToken();
      String scope = jwtUtil.validateToken(token);
      List<GrantedAuthority> authorityList =
          AuthorityUtils.commaSeparatedStringToAuthorityList(scope);
      log.info("Authority List: {}", authorityList);
      return new AuthenticatedUser(authorityList);
    } catch (Exception e) {
      log.error("Error while validating token", e);
      throw new RuntimeException(e.getMessage());
    }
  }
}
