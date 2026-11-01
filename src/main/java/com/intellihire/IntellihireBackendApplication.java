package com.intellihire;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class IntellihireBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntellihireBackendApplication.class, args);
    }

    // Bean to call external APIs
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // Bean to parse JSON data
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}