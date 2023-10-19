package com.skipcart.api;

import com.skipcart.domain.LoginRequest;
import com.skipcart.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1api/")
@Slf4j
public class AuthController2 {

  @Autowired private HttpServletRequest context;

  @Autowired private AuthService authService;

//  @Autowired private AuthenticationManager authenticationManager;

  @GetMapping("/get")
  @Secured("USER_ROLE")
  public String login2() {
    return "success";
  }
}
