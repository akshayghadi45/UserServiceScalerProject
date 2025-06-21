package org.java.userservicescalerproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class  UserServiceScalerProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceScalerProjectApplication.class, args);
    }

}
