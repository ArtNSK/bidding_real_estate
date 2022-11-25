package ru.shestakov_a.bidding_telegram_bot.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class PaginationControlValuesDTO {
    @NonNull private String cursor;
    @NonNull private int quantity;
    private int page;
}