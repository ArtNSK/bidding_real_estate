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
@Table(name = "cursors")
public class CursorEntity {
    @Id
    @Column(name = "id_cadastral_number")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "message_id")
    private Integer messageId;
    @Column(name ="cursor")
    private Integer cursor;
    @Column(name = "previous_cursor")
    private Integer previousCursor;
    @Column(name = "creation_time")
    private Long creationTime;
}
