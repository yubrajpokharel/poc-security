package com.skipcart.utils;

import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityContextUtil {

  public static void setRolesInSecurityContext(List<String> roles) {
//    // Create an Authentication object with the user's roles
//    UserDetails userDetails = new CustomUserDetails(/* Pass user details here */); // You need to implement CustomUserDetails with UserDetails
//    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, roles);
//
//    // Set the Authentication object in the SecurityContext
//    SecurityContext securityContext = SecurityContextHolder.getContext();
//    securityContext.setAuthentication(authentication);
  }
}
