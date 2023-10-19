package com.skipcart.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
@JsonPropertyOrder({"appToken", "userToken", "userId", "authorities"})
public class SkipCartUserDetail extends UsernamePasswordAuthenticationToken {

  private final String appToken;

  private final String userToken;

  private final String userId;

  private final Collection<GrantedAuthority> authorities;

  public SkipCartUserDetail(
      Collection<GrantedAuthority> authorities, String appToken, String userToken, String userId) {
    super(userId, null, authorities);
    this.appToken = appToken;
    this.userToken = userToken;
    this.userId = userId;
    this.authorities = authorities;
  }

  @Override
  public Collection<GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @SneakyThrows
  @Override
  public String toString() {
    return new ObjectMapper().writeValueAsString(this);
  }
}
