package ru.shestakov_a.bidding_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan(basePackages = "real_estate.entity")
@EnableScheduling
public class BiddingApp {

    public static void main(String[] args) {
        SpringApplication.run(BiddingApp.class, args);
    }

}
