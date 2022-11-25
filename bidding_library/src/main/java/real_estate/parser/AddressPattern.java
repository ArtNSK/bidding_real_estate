package real_estate.parser;

import lombok.Getter;
import util.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Класс содержит набор регулярных выражений для парсинга адреса
 * @author Shestakov Artem
 * */
@Getter
public class AddressPattern {
    private final Map<AddressPatternString, Pair<Pattern, Pattern>> patterns = new HashMap<>();
    private static AddressPattern instance;

    private AddressPattern() {
        Arrays.stream(AddressPatternString.values())
                .sequential()
                .forEach(addressPatternString -> {
                    String type = addressPatternString.getElement().getFirst();
                    String address = String.format(addressPatternString.getElement().getSecond(), type, type);
                    Pair<Pattern, Pattern> patternPair = new Pair<>(Pattern.compile(type), Pattern.compile(address));
                    patterns.put(addressPatternString, patternPair);
                });
    }


    public static AddressPattern getInstance() {
        if (instance == null) {
            instance = new AddressPattern();
        }
        return instance;
    }
}
