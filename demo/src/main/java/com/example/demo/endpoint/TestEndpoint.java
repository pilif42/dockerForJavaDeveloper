package com.example.demo.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test")
@Slf4j
public class TestEndpoint {

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity theGetEndpoint() {
    log.info("Entering theGetEndpoint...");
    return ResponseEntity.noContent().build();
  }
}
