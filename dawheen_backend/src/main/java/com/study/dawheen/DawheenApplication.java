package com.study.dawheen;

import com.study.dawheen.config.AppProperties;
import com.study.dawheen.config.CorsProperties;
import com.study.dawheen.config.JpaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableConfigurationProperties({
        AppProperties.class,
        CorsProperties.class
})
@Import({JpaConfig.class})
@PropertySource("classpath:application-oauth.properties")
@EnableScheduling
@EnableMongoRepositories
@SpringBootApplication
public class DawheenApplication {

    public static void main(String[] args) {
        SpringApplication.run(DawheenApplication.class, args);
    }

}

