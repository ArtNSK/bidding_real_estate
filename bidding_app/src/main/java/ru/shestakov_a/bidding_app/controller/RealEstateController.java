package ru.shestakov_a.bidding_app.controller;

import io.swagger.annotations.ApiOperation;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import real_estate.dto.controller.*;
import ru.shestakov_a.bidding_app.model.error.DefaultErrorResponse;
import ru.shestakov_a.bidding_app.service.RealEstateService;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Класс контроллера для обработки запросов к базе данных торгов по недвижимости
 * @author Shestakov Artem
 * */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/real-estate")
public class RealEstateController {
    private final String POSITIVE_NUMBER = "^\\d*";
    private final RealEstateService realEstateService;

    /**
     * GET /api/real-estate/list : Возвращает список доступных объектов недвижимости. При указании кадастрового номера, 
     *      возвращает список объектов недвижимости с переданным кадастровым номером
     *
     * @param limit Ограничение на количество объектов в ответе (required)
     * @param cursor Курсор, для получения следующей части ответа (optional)
     * @param cadastralNumber Кадастровый номер, для поиска объектов недвижимости (optional)
     * @return OK (status code 200)
     *         or Ошибка в передаваемых параметрах (status code 400)
     * @see RealEstateController#getRealEstateList(int, Optional, Optional) 
     */
    @ApiOperation(value = "Возвращает список доступных объектов недвижимости, при указании кадастровго номера, возвращает объекты недвижимости с переданным кадастровым номером", nickname = "getRealEstateList", notes = "", response = RealEstateResponseDTO.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = RealEstateResponseDTO.class),
            @ApiResponse(code = 400, message = "Ошибка в передаваемых параметрах", response = DefaultErrorResponse.class) })

    @GetMapping(
            value = "/list",
            produces = {"application/json"}
    )
    ResponseEntity<RealEstateResponseDTO<RealEstateDTO>> getRealEstateList(
            @RequestParam @Valid @Positive int limit,
            @RequestParam @Valid Optional<@Pattern(regexp = POSITIVE_NUMBER) String> cursor,
            @RequestParam Optional<String> cadastralNumber

    ) {
        return new ResponseEntity<>(realEstateService.getRealEstateList(limit, cursor, cadastralNumber), HttpStatus.OK);
    }

    /**
     * POST /api/real-estate/list/cadastral-numbers : Возвращает список объектов недвижимости по переданному 
     *      списку кадастровых номеров
     *
     * @param limit Ограничение на количество объектов в ответе (required)
     * @param cursor Курсор, для получения следующей части ответа (optional)
     * @param cadastralNumberDTO  (required)
     * @return OK (status code 200)
     *         or Ошибка в передаваемых параметрах (status code 400)
     * @see RealEstateController#getRealEstateListByCadastralNumbers(int, Optional, CadastralNumberDTO) 
     */
    @ApiOperation(value = "Возвращает список объектов недвижимости по списку кадастровых номеров", nickname = "getRealEstateListByCadastralNumbers", notes = "", response = RealEstateResponseDTO.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = RealEstateResponseDTO.class),
            @ApiResponse(code = 400, message = "Ошибка в передаваемых параметрах", response = DefaultErrorResponse.class) })
    @PostMapping(value = "/list/cadastral-numbers",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<RealEstateResponseDTO<RealEstateDTO>> getRealEstateListByCadastralNumbers(
                 @RequestParam @Valid @Positive int limit,
                 @RequestParam @Valid Optional<@Pattern(regexp = POSITIVE_NUMBER) String> cursor,
                 @RequestBody @Valid CadastralNumberDTO cadastralNumberDTO
    ) {
        List<String> cadastralNumbers = new ArrayList<>(cadastralNumberDTO.getCadastralNumbers());
        return new ResponseEntity<>(realEstateService.getRealEstateListByCadastralNumbers(limit, cursor, cadastralNumbers), HttpStatus.OK);
    }

    /**
     * GET /api/real-estate/list/city : Возвращает список объектов недвижимости по городу и региону
     *
     * @param limit Ограничение на количество объектов в ответе (required)
     * @param region Название региона (required)
     * @param city Название города (required)
     * @param cursor Курсор, для получения следующей части ответа (optional)
     * @return OK (status code 200)
     *         or Ошибка в передаваемом id (status code 400)
     * @see RealEstateController#getRealEstateListByCity(int, Optional, String, String) 
     */
    @ApiOperation(value = "Возвращает список объектов недвижимости по городу и региону", nickname = "getRealEstateListByCity", notes = "", response = RealEstateResponseDTO.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = RealEstateResponseDTO.class),
            @ApiResponse(code = 400, message = "Ошибка в передаваемом id", response = DefaultErrorResponse.class) })
    @GetMapping(
            value = "/list/city",
            produces = {"application/json"})
    ResponseEntity<RealEstateResponseDTO<RealEstateDTO>> getRealEstateListByCity(
                  @RequestParam @Valid @Positive int limit,
                  @RequestParam @Valid Optional<@Pattern(regexp = POSITIVE_NUMBER) String> cursor,
                  @RequestParam @NotBlank String region,
                  @RequestParam @NotBlank String city
    ) {
        return new ResponseEntity<>(realEstateService.getRealEstateListByCity(limit, cursor, region, city), HttpStatus.OK);
    }

    /**
     * GET /api/real-estate/list/region : Возвращает список объектов недвижимости по региону
     *
     * @param limit Ограничение на количество объектов в ответе (required)
     * @param region Название региона (required)
     * @param cursor Курсор, для получения следующей части ответа (optional)
     * @return OK (status code 200)
     *         or Ошибка в передаваемом id (status code 400)
     * @see RealEstateController#getRealEstateListByRegion(int, Optional, String) 
     */
    @ApiOperation(value = "Возвращает список объектов недвижимости по региону", nickname = "getRealEstateListByRegion", notes = "", response = RealEstateResponseDTO.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = RealEstateResponseDTO.class),
            @ApiResponse(code = 400, message = "Ошибка в передаваемом id", response = DefaultErrorResponse.class) })
    @GetMapping(
            value = "/list/region",
            produces = {"application/json"})
    ResponseEntity<RealEstateResponseDTO<RealEstateDTO>> getRealEstateListByRegion(
                    @RequestParam @Valid @Positive int limit,
                    @RequestParam @Valid Optional<@Pattern(regexp = POSITIVE_NUMBER) String> cursor,
                    @RequestParam @NotBlank String region
    ) {
        return new ResponseEntity<>(realEstateService.getRealEstateListByRegion(limit, cursor, region), HttpStatus.OK);
    }

    /**
     * GET /api/real-estate/{realEstateId} : Возвращает объект недвижимости по id
     *
     * @param realEstateId Идентификатор объекта недвижимости (required)
     * @return OK (status code 200)
     *         or Ошибка в передаваемом id (status code 400)
     * @see RealEstateController#getRealEstateById(String) 
     */
    @ApiOperation(value = "Возвращает объект недвижимости по id", nickname = "getRealEstateById", notes = "", response = RealEstateDTO.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = RealEstateDTO.class),
            @ApiResponse(code = 400, message = "Ошибка в передаваемом id", response = DefaultErrorResponse.class) })
    @GetMapping(
            value = "/{realEstateId}",
            produces = {"application/json"}
    )
    ResponseEntity<RealEstateDTO> getRealEstateById(
                    @Valid @Pattern(regexp = POSITIVE_NUMBER) @PathVariable("realEstateId") String realEstateId
    ) {
        return new ResponseEntity<>(realEstateService.getRealEstateById(realEstateId), HttpStatus.OK);
    }

    /**
     * GET /api/real-estate/quantity/cadastral-number : Возвращает число объектов с запрашиваемым кадастроым номером
     *
     * @param cadastralNumber Кадастроый номер (required)
     * @return OK (status code 200)
     *         or Ошибка в передаваемых параметрах (status code 400)
     * @see RealEstateController#getRealEstateQuantityByCadastralNumber(String) 
     */
    @ApiOperation(value = "Возвращает число объектов с запрашиваемым кадастроым номером", nickname = "getRealEstateQuantityByCadastralNumber", notes = "", response = QuantityRealEstateCadastralNumberDTO.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = QuantityRealEstateCadastralNumberDTO.class),
            @ApiResponse(code = 400, message = "Ошибка в передаваемых параметрах", response = DefaultErrorResponse.class) })
    @GetMapping(
            value = "/quantity/cadastral-number",
            produces = {"application/json"}
    )
    ResponseEntity<QuantityRealEstateCadastralNumberDTO> getRealEstateQuantityByCadastralNumber(
                    @RequestParam @NotBlank String cadastralNumber
    ) {
        return new ResponseEntity<>(realEstateService.getRealEstateQuantityByCadastralNumber(cadastralNumber), HttpStatus.OK);
    }

    /**
     * GET /api/real-estate/quantity/region : Возвращает число объектов по региону
     *
     * @param region Регион (required)
     * @return OK (status code 200)
     *         or Ошибка в передаваемых параметрах (status code 400)
     * @see RealEstateController#getRealEstateQuantityByRegion(String) 
     */
    @GetMapping(
            value = "/quantity/region",
            produces = {"application/json"}
    )
    ResponseEntity<QuantityRealEstateRegionDTO> getRealEstateQuantityByRegion(
                    @RequestParam @NotBlank String region
    ) {
        return new ResponseEntity<>(realEstateService.getRealEstateQuantityByRegion(region), HttpStatus.OK);
    }

    /**
     * GET /api/real-estate/quantity/city : Возвращает число объектов по городу
     *
     * @param region Регион (required)
     * @param city Город (required)
     * @return OK (status code 200)
     *         or Ошибка в передаваемых параметрах (status code 400)
     * @see RealEstateController#getRealEstateQuantityByCity(String, String) 
     */
    @ApiOperation(value = "Возвращает число объектов по городу", nickname = "getRealEstateQuantityByCity", notes = "", response = QuantityRealEstateCityDTO.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = QuantityRealEstateCityDTO.class),
            @ApiResponse(code = 400, message = "Ошибка в передаваемых параметрах", response = DefaultErrorResponse.class) })
    @GetMapping(
            value = "/quantity/city",
            produces = {"application/json"}
    )
    ResponseEntity<QuantityRealEstateCityDTO> getRealEstateQuantityByCity(
                    @RequestParam @NotBlank String region,
                    @RequestParam @NotBlank String city
    ) {
        return new ResponseEntity<>(realEstateService.getRealEstateQuantityByCity(region, city), HttpStatus.OK);
    }

    /**
     * GET /api/real-estate/quantity/all : Возвращает общее число объектов недвижимости
     *
     * @return OK (status code 200)
     *         or Ошибка в передаваемых параметрах (status code 400)
     * @see RealEstateController#getRealEstateAllQuantity()
     */
    @ApiOperation(value = "Возвращает общее число объектов в базе данны", nickname = "getRealEstateAllQuantity", notes = "", response = QuantityRealEstateCityDTO.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = QuantityRealEstateCityDTO.class),
            @ApiResponse(code = 400, message = "Ошибка в передаваемых параметрах", response = DefaultErrorResponse.class) })
    @GetMapping(
            value = "/quantity/all",
            produces = {"application/json"}
    )
    ResponseEntity<QuantityDTO> getRealEstateAllQuantity() {
        return new ResponseEntity<>(realEstateService.getRealEstateAllQuantity(), HttpStatus.OK);
    }
}
