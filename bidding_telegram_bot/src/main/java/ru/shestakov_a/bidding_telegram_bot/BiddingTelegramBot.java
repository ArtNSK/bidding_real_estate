package ru.shestakov_a.bidding_telegram_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableFeignClients
@EnableScheduling
public class BiddingTelegramBot {

    public static void main(String[] args) {
        SpringApplication.run(BiddingTelegramBot.class, args);
    }

}
