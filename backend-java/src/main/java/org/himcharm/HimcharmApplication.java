package org.himcharm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HimcharmApplication {

    public static void main(String[] args) {
        SpringApplication.run(HimcharmApplication.class, args);
    }

}
