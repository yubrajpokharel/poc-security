package com.skipcart.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1api/")
@Slf4j
public class AuthController2 {

  @GetMapping("/permission/yes")
  @PreAuthorize("hasAuthority('Orders:Delete')")
  public String iHavePermission() {
    return "Delete Success";
  }

  @GetMapping("/permission/no")
  @PreAuthorize("hasAuthority('Orders:DeleteMeToo')")
  public String iDoNotHavePermission() {
    return "You do not have permission to delete";
  }
}
