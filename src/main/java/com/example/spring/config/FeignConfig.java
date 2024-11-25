package com.example.spring.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class FeignConfig {
    @Bean
    Logger.Level feignLogger() {
        return Logger.Level.FULL;
    }
}
