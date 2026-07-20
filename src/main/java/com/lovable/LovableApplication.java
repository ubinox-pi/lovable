package com.lovable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LovableApplication {

    static void main(String[] args) {
        SpringApplication.run(LovableApplication.class, args);
    }

}
