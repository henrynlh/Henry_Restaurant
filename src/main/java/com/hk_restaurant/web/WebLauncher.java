package com.hk_restaurant.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;

@SpringBootApplication
public class WebLauncher {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        // Do not inherit a generic DEBUG environment variable as Spring's startup debug switch.
        System.setProperty("debug", System.getProperty("debug", "false"));
        SpringApplication.run(WebLauncher.class, args);
    }
}
