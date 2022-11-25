package ru.shestakov_a.bidding_db_loader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "real_estate.entity")
public class BiddingDbLoader {

    public static void main(String[] args) {
        SpringApplication.run(BiddingDbLoader.class, args);
    }

}
