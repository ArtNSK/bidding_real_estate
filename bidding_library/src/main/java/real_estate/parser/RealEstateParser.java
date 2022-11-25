package real_estate.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import real_estate.dto.parser.*;
import real_estate.entity.AddressEntity;
import real_estate.entity.RealEstateEntity;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс gля парсинга сущности объекта недвижимости из JSON объекта
 * @author Shestakov Artem
 * */
@Slf4j
@AllArgsConstructor
public class RealEstateParser implements Callable<List<RealEstateEntity>> {
    private static final String JSON_BIDDING_TYPE = "Реализация имущества должников";
    private static final String JSON_CATEGORY_CODE = "9";
    private static final Pattern CADASTRAL_NUMBER_PATTERN = Pattern.compile("[0-9 ]+(?:[:;]+[0-9 ]+){2,}");
    private final InputStream inputStream;

    @Override
    public List<RealEstateEntity> call() {
        List<RealEstateEntity> realEstateEntities = new ArrayList<>();
        try (inputStream) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(inputStream);
            JsonNodeDTO jsonNodeDTO = getJsonNodeDTO(rootNode);

            if (isCorrectBiddingType(jsonNodeDTO)) {
                Iterator<JsonNode> lots = rootNode.get("exportObject").get("structuredObject").get("notice")
                        .get("lots").elements();
                while (lots.hasNext()) {
                    JsonNode lot = lots.next();
                    String code = lot.get("biddingObjectInfo").get("category").get("code").textValue();
                    if (isCorrectCategoryCode(code)) {
                        RealEstateEntity realEstateEntity = prepareRealEstate(jsonNodeDTO, lot, mapper);
                        realEstateEntities.add(realEstateEntity);
                    }
                }
            }

        } catch (Exception e) {
            log.info("Exception {} of parsing: {} ", e.getClass().getName(), e.getMessage()/*, fileName*/);
        }
        return realEstateEntities;
    }

    /**
     * Метод возвращает DTO, содержащее ноды {@link JsonNode} значения которых используются для парсинга,
     * сделано для большей читаемости кода
     * */
    private JsonNodeDTO getJsonNodeDTO(JsonNode rootNode) {
        JsonNode commonInfo = rootNode.get("exportObject").get("structuredObject").get("notice").get("commonInfo");
        return JsonNodeDTO.builder()
                .commonInfo(commonInfo)
                .biddCondition(rootNode.get("exportObject").get("structuredObject").get("notice").get("biddConditions"))
                .biddType(commonInfo.get("biddType").get("name"))
                .build();
    }

    private boolean isCorrectBiddingType(JsonNodeDTO jsonNodeDTO) {
        return jsonNodeDTO.getBiddType().textValue().equals(JSON_BIDDING_TYPE);
    }

    private boolean isCorrectCategoryCode(String code) {
        return code.equals(JSON_CATEGORY_CODE);
    }

    /**
     * Метод возвращает сущность объекта недвижимости
     * */
    private RealEstateEntity prepareRealEstate(JsonNodeDTO jsonNodeDTO, JsonNode lot, ObjectMapper mapper) {
        LotDataDTO lotDataDTO = getLotData(jsonNodeDTO.getCommonInfo(), lot);
        LotAddressDTO lotAddressDTO = getLotAddress(lot);
        LotDatesDTO lotDatesDTO = getLotDates(jsonNodeDTO.getCommonInfo(), jsonNodeDTO.getBiddCondition());
        List<CharacteristicDTO> characteristicList = handleCharacteristicJsonNode(lot, mapper);
        String cadastralNumber = getCadastralNumber(lotDataDTO.getLotName(), characteristicList);

        return getRealEstate(lotAddressDTO, lotDataDTO, lotDatesDTO, cadastralNumber);
    }

    /**
     * Метод создает DTO с информацией по объекту недвижимости
     * */
    private LotDataDTO getLotData(JsonNode commonInfo, JsonNode lot) {
        String lotName = lot.get("lotName").textValue();
        String realEstateType = parseRealEstateType(lotName);

        return LotDataDTO.builder()
                .lotNumber(lot.get("lotNumber").asInt())
                .priceMin(lot.get("priceMin").textValue())
                .link(commonInfo.get("href").textValue())
                .lotName(lotName)
                .realEstateType(realEstateType)
                .build();
    }

    /**
     * Метод для парсинга типа объекта недвижимости
     * */
    private String parseRealEstateType(String lotName) {
        lotName = lotName.toLowerCase();
        if (lotName.contains("доля") || lotName.contains("доли")) {
            return RealEstateType.OWNERSHIP_SHARE.getValue();
        } else if (lotName.contains("комната")) {
            return RealEstateType.ROOM.getValue();
        } else if (lotName.contains("квартир") || lotName.contains("помещение")) {
            return RealEstateType.APARTMENT.getValue();
        } else if (lotName.contains("дом") || lotName.contains("здание")) {
            return RealEstateType.BUILDING.getValue();
        } else return RealEstateType.REAL_ESTATE.getValue();
    }

    /**
     * Метод создает DTO с информацией по адресу объекта
     * */
    private LotAddressDTO getLotAddress(JsonNode lot) {
        String address = lot.get("biddingObjectInfo").get("estateAddress").textValue();
        String subjectRF = lot.get("biddingObjectInfo").get("subjectRF").get("name").textValue();
        if (subjectRF != null) {
            address = subjectRF + ", " + address;
        }
        return LotAddressDTO.builder()
                .address(address)
                .build();
    }

    /**
     * Метод создает DTO с информацией по датам объекта (дата проведения аукциона, дата подачи заявки и т.д.)
     * */
    private LotDatesDTO getLotDates(JsonNode commonInfo, JsonNode biddConditions) {
        return LotDatesDTO.builder()
                .publishDate(LocalDateTime.ofInstant(Instant.parse(commonInfo.get("publishDate").textValue()), ZoneId.ofOffset("UTC", ZoneOffset.of("+03"))) )
                .biddingStartTime(LocalDateTime.ofInstant(Instant.parse(biddConditions.get("biddStartTime").textValue()), ZoneId.ofOffset("UTC", ZoneOffset.of("+03"))))
                .biddingEndTime(LocalDateTime.ofInstant(Instant.parse(biddConditions.get("biddEndTime").textValue()), ZoneId.ofOffset("UTC", ZoneOffset.of("+03"))))
                .auctionStartDate(LocalDateTime.ofInstant(Instant.parse(biddConditions.get("startDate").textValue()), ZoneId.ofOffset("UTC", ZoneOffset.of("+03"))))
                .build();
    }

    /**
     * Метод создает список характеристик для лота
     * */
    private List<CharacteristicDTO> handleCharacteristicJsonNode(JsonNode lot, ObjectMapper mapper) {
        List<CharacteristicDTO> characteristicList = new ArrayList<>();
        Iterator<JsonNode> characteristics = lot.get("biddingObjectInfo").get("characteristics").elements();
        while (characteristics.hasNext()) {
            JsonNode value = characteristics.next();
            try {
                characteristicList.add(mapper.readValue(value.toString(), CharacteristicDTO.class));
            } catch (Exception e) {
                log.info("Exception {} of parsing characteristic list: {} ", e.getClass().getName(), e.getMessage());
            }
        }
        return characteristicList;
    }

    /**
     * Метод возвращает сущность объекта недвижимости
     * */
    private RealEstateEntity getRealEstate(LotAddressDTO lotAddressDTO, LotDataDTO lotDataDTO, LotDatesDTO lotDatesDTO, String cadastralNumber) {
        AddressParser addressParser = new AddressParser(lotAddressDTO.getAddress());
        AddressEntity address = addressParser.parseAddress();
        return RealEstateEntity.builder()
                /*.addressReal(lotAddressDTO.getAddress())*/
                .address(address)
                .cadastralNumber(cadastralNumber)
                .link(lotDataDTO.getLink())
                .lotName(lotDataDTO.getLotName())
                .lotNumber(lotDataDTO.getLotNumber())
                .minPrice(lotDataDTO.getPriceMin())
                .realEstateType(lotDataDTO.getRealEstateType())
                .publishDate(lotDatesDTO.getPublishDate())
                .biddingStartTime(lotDatesDTO.getBiddingStartTime())
                .biddingEndTime(lotDatesDTO.getBiddingEndTime())
                .auctionStartDate(lotDatesDTO.getAuctionStartDate())
                .id(RealEstateCounter.getInstance().realEstateId.incrementAndGet())
                .build();
    }

    /**
     * Метод возвращает кадастровый номер либо из названия лота, либо из характеристик лота
     * */
    private String getCadastralNumber(String lotName, List<CharacteristicDTO> characteristicDTOList) {
        Optional<String> cadastralNumber = parseCadastralNumber(lotName);
        if (cadastralNumber.isEmpty()) {
            for (CharacteristicDTO cn : characteristicDTOList) {
                cadastralNumber = parseCadastralNumber(cn.getCharacteristicValue());
                if (cadastralNumber.isPresent()) {
                    break;
                }
            }
        }
        return cadastralNumber.map(String::trim).orElse(null);
    }

    /**
     * Метод парсит кадастровый номер в передаваемой строки
     * */
    private Optional<String> parseCadastralNumber(String inputString) {
        Matcher matcher = CADASTRAL_NUMBER_PATTERN.matcher(inputString);
        if (matcher.find()) {
            inputString = matcher.group();
        } else {
            return Optional.empty();
        }
        return Optional.of(inputString);
    }
}