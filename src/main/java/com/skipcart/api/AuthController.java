package com.skipcart.api;

import com.skipcart.domain.LoginRequest;
import com.skipcart.dto.User;
import com.skipcart.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/")
@Slf4j
public class AuthController {

  @Autowired private HttpServletRequest context;

  @Autowired private AuthService authService;

  @Autowired private AuthenticationManager authenticationManager;

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginRequest request) {
    try {
      authService.authenticate(request.getEmail(), request.getPassword());
      return ResponseEntity.ok("Login successful");
    } catch (AuthenticationException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
    }
  }

  @GetMapping("/get")
  @Secured("USER_ROLE")
  public String login2() {
    return "success";
  }
}
