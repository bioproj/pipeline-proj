package com.bioproj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
//@EnableJpaRepositories(basePackages = {"com.bioproj"})
//@EntityScan(basePackages = {"com.bioproj"})
//@EnableFeignClients(basePackages = {"com.mbiolance.cloud.platform.rpc","com.mbiolance.cloud.auth.rpc"})
//@EnableAsync
//@EnableUserInfoTransmitter
public class Application {

//    public static void main(String[] args) {
//        SpringApplication.run(Application.class, args);
//    }

}
