package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@SpringBootApplication
@RestController
public class BeanstalkDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(BeanstalkDemoApplication.class, args);
  }

  private String id = UUID.randomUUID().toString();

  @GetMapping("/")
  public ResponseEntity<String> get() {
    String port = System.getenv("PORT");
    String serverPort = System.getenv("SERVER_PORT");
    String managementServerPort = System.getenv("MANAGEMENT_SERVER_PORT");
    String response = String.format("Hello Beanstalk - v2 - %s<br>$PORT=%s<br>$SERVER_PORT=%s<br>$MANAGEMENT_SERVER_PORT=%s",
        id,
        port,
        serverPort,
        managementServerPort);
    return ResponseEntity.ok().body(response);
  }
}
