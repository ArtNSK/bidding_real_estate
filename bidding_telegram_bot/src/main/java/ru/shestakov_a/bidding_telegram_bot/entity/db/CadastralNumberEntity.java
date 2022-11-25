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
@Table(name = "cadastral_number")
public class CadastralNumberEntity {
    @Id
    @Column(name = "id_cadastral_number")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "message_id")
    private Integer messageId;
    @Column(name = "cadastral_number")
    private String cadastralNumber;
    @Column(name = "creation_time")
    private Long creationTime;
}
