package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SpringBootApplication
@RestController
@Slf4j
public class BeanstalkDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(BeanstalkDemoApplication.class, args);
  }

  private String id = UUID.randomUUID().toString();
  @Autowired
  private CustomerRepository customerRepository;

  @PostConstruct
  public void postConstruct(){
    long count = customerRepository.count();
    log.info("{} entities already present in the database", count);
    if(count == 0){
      customerRepository.save(new Customer("theFirstName", "theLastName"));
    }
  }

  @GetMapping("/")
  public ResponseEntity<String> get() {
    String port = System.getenv("PORT");
    String serverPort = System.getenv("SERVER_PORT");
    String managementServerPort = System.getenv("MANAGEMENT_SERVER_PORT");
    String entities = StreamSupport.stream(customerRepository.findAll().spliterator(),false)
        .map(Objects::toString)
        .collect(Collectors.joining(","));
    String response = String.format("Hello Beanstalk - v3 - %s<br>$PORT=%s<br>$SERVER_PORT=%s<br>$MANAGEMENT_SERVER_PORT=%s<br>%s",
        id,
        port,
        serverPort,
        managementServerPort,
        entities);
    return ResponseEntity.ok().body(response);
  }

}
