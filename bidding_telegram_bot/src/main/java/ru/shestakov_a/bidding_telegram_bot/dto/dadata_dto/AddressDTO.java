package ru.shestakov_a.bidding_telegram_bot.dto.dadata_dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Data
@ToString
public class AddressDTO<T> {
    @JsonAlias("value")
    private String value;

    @JsonAlias("unrestricted_value")
    private String unrestrictedValue;

    @JsonAlias("data")
    private DataAddressDTO data;

}
