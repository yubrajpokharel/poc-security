package com.skipcart.api;

import com.skipcart.domain.LoginRequest;
import com.skipcart.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<Object> login(
      @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
      return ResponseEntity.ok(
          authService.authenticate(
              loginRequest.getEmail(), loginRequest.getPassword(), request.getHeader("appToken")));
  }
}
