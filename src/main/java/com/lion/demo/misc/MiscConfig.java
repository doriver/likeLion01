package com.lion.demo.misc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MiscConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
