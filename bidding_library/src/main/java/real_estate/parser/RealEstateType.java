package real_estate.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum в котором содержатся типы объектов недвижимости
 * @author Shestakov Artem
 * */
@RequiredArgsConstructor
@Getter
public enum RealEstateType {
    OWNERSHIP_SHARE("Доля в праве собственности"),
    ROOM("Комната"),
    APARTMENT("Квартира"),
    BUILDING("Дом"),
    REAL_ESTATE("Объект недвижимости");
    private final String value;
}
