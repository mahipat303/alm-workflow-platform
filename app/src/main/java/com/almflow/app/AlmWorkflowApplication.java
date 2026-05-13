package com.almflow.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.almflow.app", "com.almflow.connector"})
public class AlmWorkflowApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlmWorkflowApplication.class, args);
    }
}
