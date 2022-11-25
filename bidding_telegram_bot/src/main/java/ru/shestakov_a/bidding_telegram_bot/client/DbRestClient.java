package ru.shestakov_a.bidding_telegram_bot.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import real_estate.dto.controller.*;

import java.util.Optional;

/**
 * Интерфейс клиента для обращения к серверу приложения торгов по недвижимости
 * @author Shestakov Artem
 * */
@FeignClient(
        value = "database",
        url = "${client.db.url}"
)
public interface DbRestClient{

    /**
     * GET /api/real-estate/list : Возвращает список доступных объектов недвижимости
     *
     * @param limit Ограничение на количество объектов в ответе (required)
     * @param cursor Курсор, для получения следующей части ответа (optional)
     * @return OK (status code 200)
     *         or Ошибка в передаваемых параметрах (status code 400)
     */
    @GetMapping(value ="/list" )
    Optional<RealEstateResponseDTO<RealEstateDTO>> getAllRealEstateList(
            @RequestParam("limit") int limit,
            @RequestParam("cursor") String cursor
    );


    /**
     * GET /api/real-estate/list : Возвращает список доступных объектов недвижимости с переданным кадастровым номером
     *
     * @param limit Ограничение на количество объектов в ответе (required)
     * @param cursor Курсор, для получения следующей части ответа (optional)
     * @param cadastralNumber Кадастровый номер, для поиска объектов недвижимости (optional)
     * @return OK (status code 200)
     *         or Ошибка в передаваемых параметрах (status code 400)
     */
    @GetMapping(value ="/list" )
    Optional<RealEstateResponseDTO<RealEstateDTO>> getRealEstateList(
            @RequestParam("limit") int limit,
            @RequestParam("cursor") String cursor,
            @RequestParam("cadastralNumber") String cadastralNumber
            );

    /**
     * POST /api/real-estate/list/cadastral-numbers : Возвращает список объектов недвижимости по переданному
     *      списку кадастровых номеров
     *
     * @param limit Ограничение на количество объектов в ответе (required)
     * @param cursor Курсор, для получения следующей части ответа (optional)
     * @param cadastralNumberDTO  (optional)
     * @return OK (status code 200)
     *         or Ошибка в передаваемых параметрах (status code 400)
     */
    @PostMapping(value = "/list/cadastral-numbers")
    Optional<RealEstateResponseDTO<RealEstateDTO>> getRealEstateListByCadastralNumbers(
            @RequestParam("limit") int limit,
            @RequestParam("cursor") String cursor,
            @RequestBody CadastralNumberDTO cadastralNumberDTO
            );

    /**
     * GET /api/real-estate/{realEstateId} : Возвращает объект недвижимости по id
     *
     * @param realEstateId Идентификатор объекта недвижимости (required)
     * @return OK (status code 200)
     *         or Ошибка в передаваемом id (status code 400)
     */
    @GetMapping(value = "/{realEstateId}")
    Optional<RealEstateDTO> getRealEstateById(
            @PathVariable("realEstateId") String realEstateId
    );

    /**
     * GET /api/real-estate/quantity/cadastral-number : Возвращает число объектов с запрашиваемым кадастроым номером
     *
     * @param cadastralNumber Кадастроый номер (required)
     * @return OK (status code 200)
     *         or Ошибка в передаваемых параметрах (status code 400)
     */
    @GetMapping(value = "/quantity/cadastral-number")
    Optional<QuantityRealEstateCadastralNumberDTO> getRealEstateQuantityByCadastralNumber(
            @RequestParam("cadastralNumber") String cadastralNumber
    ) ;

    /**
     * GET /api/real-estate/list/region : Возвращает список объектов недвижимости по региону
     *
     * @param limit Ограничение на количество объектов в ответе (required)
     * @param region Название региона (required)
     * @param cursor Курсор, для получения следующей части ответа (optional)
     * @return OK (status code 200)
     *         or Ошибка в передаваемом id (status code 400)
     */
    @GetMapping(value = "/list/region")
    Optional<RealEstateResponseDTO<RealEstateDTO>> getRealEstateListByRegion(
            @RequestParam int limit,
            @RequestParam  String cursor,
            @RequestParam String region
    );

    /**
     * GET /api/real-estate/list/city : Возвращает список объектов недвижимости по городу и региону
     *
     * @param limit Ограничение на количество объектов в ответе (required)
     * @param region Название региона (required)
     * @param city Название города (required)
     * @param cursor Курсор, для получения следующей части ответа (optional)
     * @return OK (status code 200)
     *         or Ошибка в передаваемом id (status code 400)
     */
    @GetMapping(
            value = "/list/city")
    Optional<RealEstateResponseDTO<RealEstateDTO>> getRealEstateListByCity(
            @RequestParam  int limit,
            @RequestParam String cursor,
            @RequestParam String region,
            @RequestParam String city
    );

    /**
     * GET /api/real-estate/quantity/city : Возвращает число объектов по городу
     *
     * @param region Регион (required)
     * @param city Город (required)
     * @return OK (status code 200)
     *         or Ошибка в передаваемых параметрах (status code 400)
     */
    @GetMapping(value = "/quantity/city")
    Optional<QuantityRealEstateCityDTO> getRealEstateQuantityByCity(
            @RequestParam String region,
            @RequestParam String city
    );

    /**
     * GET /api/real-estate/quantity/region : Возвращает число объектов по региону
     *
     * @param region Регион (required)
     * @return OK (status code 200)
     *         or Ошибка в передаваемых параметрах (status code 400)
     */
    @GetMapping(
            value = "/quantity/region",
            produces = {"application/json"}
    )
    Optional<QuantityRealEstateRegionDTO> getRealEstateQuantityByRegion(
            @RequestParam String region
    );

    /**
     * GET /api/real-estate/quantity/all : Возвращает общее число объектов недвижимости
     *
     * @return OK (status code 200)
     *         or Ошибка в передаваемых параметрах (status code 400)
     */
    @GetMapping(
            value = "/quantity/all",
            produces = {"application/json"}
    )
    Optional<QuantityDTO> getRealEstateAllQuantity();
}