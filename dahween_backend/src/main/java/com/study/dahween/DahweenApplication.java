package com.study.dahween;

import com.study.dahween.config.JpaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Import({JpaConfig.class})
@PropertySource("classpath:application-oauth.properties")
@SpringBootApplication
public class DahweenApplication {

    public static void main(String[] args) {
        SpringApplication.run(DahweenApplication.class, args);
    }

}

