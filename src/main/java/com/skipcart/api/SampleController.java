package com.skipcart.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class SampleController {

  @GetMapping
  public String hello(){
    return "Hello user";
  }


}
