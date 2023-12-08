package com.memotalk;

import com.memotalk.oauth.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableConfigurationProperties(AppProperties.class)
@EnableWebSecurity
@SpringBootApplication
public class MemotalkApplication {
    public static void main(String[] args) {
        SpringApplication.run(MemotalkApplication.class, args);
    }
}
