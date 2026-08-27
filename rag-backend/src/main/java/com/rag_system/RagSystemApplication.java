package com.rag_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RagSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(RagSystemApplication.class, args);
    }

}
