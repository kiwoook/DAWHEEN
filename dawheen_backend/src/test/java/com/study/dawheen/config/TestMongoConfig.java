package com.study.dawheen.config;

import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

@TestConfiguration
public class TestMongoConfig {

    @Value("${spring.data.mongodb.uri}")
    String connectionString;

    @Bean
    @Primary
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(MongoClients.create(connectionString), "dawheen_db");
    }
}
