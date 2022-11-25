package ru.shestakov_a.bidding_telegram_bot.entity.db;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "auction_information")
public class AuctionInformationEntity {
    @Id
    @Column(name = "id_auction_information")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "message_id")
    private Integer messageId;
    @Column(name ="cursor")
    private Integer cursor;
    @Column(name = "bidding_start_time")
    private LocalDateTime biddingStartTime;
    @Column(name = "bidding_end_time")
    private LocalDateTime biddingEndTime;
    @Column(name = "auction_start_date")
    private LocalDateTime auctionStartDate;
    @Column(name = "creation_time")
    private Long creationTime;
}
