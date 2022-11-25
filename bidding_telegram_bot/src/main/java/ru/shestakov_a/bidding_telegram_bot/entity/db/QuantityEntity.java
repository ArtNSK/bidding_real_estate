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
@Table(name = "quantity")
public class QuantityEntity {
    @Id
    @Column(name = "id_quantity")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "message_id")
    private Integer messageId;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "creation_time")
    private Long creationTime;

}
