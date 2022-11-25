package ru.shestakov_a.bidding_telegram_bot.entity.db;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "location_type")
public class LocationTypeEntity {
    @Id
    @Column(name = "id_location_type")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "message_id")
    private Integer messageId;
    @Column(name = "locaiton_type")
    private Integer locationType;
    @Column(name = "creation_time")
    private Long creationTime;
}
