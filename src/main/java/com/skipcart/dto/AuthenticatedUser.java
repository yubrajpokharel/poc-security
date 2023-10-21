package com.skipcart.dto;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticatedUser implements UserDetails {

  private final Collection<? extends GrantedAuthority> authorities;

  public AuthenticatedUser(List<GrantedAuthority> authorityList) {
    this.authorities = authorityList;
  }

  /**
   * @return - Collection<? extends GrantedAuthority>
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  /**
   * @return - String
   */
  @Override
  public String getPassword() {
    return null;
  }

  /**
   * @return - String
   */
  @Override
  public String getUsername() {
    return null;
  }

  /**
   * @return - boolean
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * @return - boolean
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * @return - boolean
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * @return - boolean
   */
  @Override
  public boolean isEnabled() {
    return true;
  }
}
