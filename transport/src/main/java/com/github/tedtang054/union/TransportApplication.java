package com.github.tedtang054.union;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author: dengJh
 * @Date: 2024/04/23 13:39
 */
@Configuration
@ComponentScan("com.github.tedtang054")
@EnableScheduling
public class TransportApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TransportApplication.class);
    }

}
