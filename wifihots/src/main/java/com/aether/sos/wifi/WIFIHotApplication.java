package com.aether.sos.wifi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WIFIHotApplication {

    public static void main(String[] args) {
        SpringApplication.run(WIFIHotApplication.class, args);
    }

}
