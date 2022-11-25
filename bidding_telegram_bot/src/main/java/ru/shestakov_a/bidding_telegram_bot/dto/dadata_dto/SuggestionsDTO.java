package ru.shestakov_a.bidding_telegram_bot.dto.dadata_dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Data
@Getter
@Setter
@ToString
public class SuggestionsDTO<T> {
    @JsonAlias("suggestions")
    List<T> suggestions;
}
