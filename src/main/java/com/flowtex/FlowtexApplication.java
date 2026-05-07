package com.flowtex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.flowtex")
@EntityScan(basePackages = "com.flowtex")
@EnableJpaRepositories(basePackages = "com.flowtex")
@EnableJpaAuditing
public class FlowtexApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowtexApplication.class, args);
    }
}
