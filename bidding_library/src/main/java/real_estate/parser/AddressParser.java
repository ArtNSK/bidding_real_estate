package real_estate.parser;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.Pair;
import real_estate.entity.AddressEntity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для парсинга адреса из переданной строки
 *
 * @author Shestakov Artem
 */
@AllArgsConstructor
@Slf4j
public class AddressParser {
    private String address;

    public AddressEntity parseAddress() {
        AddressPattern addressPattern = AddressPattern.getInstance();
        address = address.toLowerCase();

        String region = parseRegion(address);
        String city = parse(AddressPatternString.CITY, address, addressPattern);
        String district = parse(AddressPatternString.DISTRICT, address, addressPattern);
        String microdistrict = parse(AddressPatternString.MICRODISTRICT, address, addressPattern);
        String street = parse(AddressPatternString.STREET, address, addressPattern);
        String building = parse(AddressPatternString.BUILDING, address, addressPattern);
        String housing = parse(AddressPatternString.HOUSING, address, addressPattern);
        String apartment = parse(AddressPatternString.APARTMENT, address, addressPattern);
        String room = parse(AddressPatternString.ROOM, address, addressPattern);
        return AddressEntity.builder()
                .region(region)
                .district(district)
                .city(city)
                .microdistrict(microdistrict)
                .street(street)
                .building(building)
                .housing(housing)
                .apartment(apartment)
                .room(room)
                .build();
    }

    private String parseRegion(String address) {
        address = address.toLowerCase();
        if (address.contains("область")) {
            return address.substring(0, address.indexOf("область")).trim();
        } else if (address.contains("республика")) {
            return address
                    .substring(0, address.indexOf(","))
                    .replaceAll("республика", "")
                    .trim();
        } else if (address.contains("край")) {
            return address.substring(0, address.indexOf("край")).trim();
        } else if (address.contains("москва") || address.contains("санкт")) {
            return address.substring(address.indexOf("г") + 1, address.indexOf(",")).replaceAll("\\.", "").trim();
        }
        return null;
    }

    /**
     * Метод парсит адрес по передаваемому шаблону для города, района, микрорайона, улицы, здания, корпуса, квартиры и
     * команты.
     *
     * @param patternString Enum со строковым шаблоном регулярного выражения для парсинга соответсвующей категории адреса
     * @param address     Входящая строка для парсинга
     * @param addressPattern Класс содержит регулярные выражения для парсинга
     */
    private String parse(AddressPatternString patternString, String address, AddressPattern addressPattern/*Pair<Pattern, Pattern> patternPair*/) {
        Pair<Pattern, Pattern> patternPair = addressPattern.getPatterns().get(patternString);
        Pattern elementTypePattern = patternPair.getFirst();
        Pattern elementPattern = patternPair.getSecond();

        Matcher matcherElementType = elementTypePattern.matcher(address);
        if (matcherElementType.find()) {
            Matcher matcherElement = elementPattern.matcher(address);
            if (matcherElement.find()) {
                return matcherElement.group(3) != null ? matcherElement.group(3).trim() : matcherElement.group(5).trim();
            }
        }
        return null;
    }

}
