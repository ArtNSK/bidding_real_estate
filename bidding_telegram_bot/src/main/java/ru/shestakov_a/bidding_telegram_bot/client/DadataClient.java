package ru.shestakov_a.bidding_telegram_bot.client;

import ru.shestakov_a.bidding_telegram_bot.dto.GeolocationDTO;
import ru.shestakov_a.bidding_telegram_bot.dto.dadata_dto.AddressDTO;
import ru.shestakov_a.bidding_telegram_bot.dto.dadata_dto.SuggestionsDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Интерфейс клиента для обращения к сервису геокодирования @see href<https://dadata.ru/api/geolocate/>
 * @author Shestakov Artem
 * */
@FeignClient(
        value = "dadata",
        url="${client.dadata.url}"
)
@Headers({"Content-Type: application/json"})
public interface DadataClient {
    @PostMapping(value = "/address")
    SuggestionsDTO<AddressDTO> getAddress(@RequestHeader(value = "Authorization", required = true) String authorizationHeader, @RequestBody GeolocationDTO geolocationDTO);

}
