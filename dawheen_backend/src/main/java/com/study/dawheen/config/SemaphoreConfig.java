package com.study.dawheen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Semaphore;

@Configuration
public class SemaphoreConfig {

    @Bean
    public Semaphore binarySemaphore(){
        return new Semaphore(1, true);
    }
}
