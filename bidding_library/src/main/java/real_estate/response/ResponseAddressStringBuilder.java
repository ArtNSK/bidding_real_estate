package real_estate.response;

import real_estate.dto.controller.AddressDTO;

/**
 * Класс билдера, в котором генерируется строка адреса объекта недвижимости
 * @author Shestakov Artem
 * */
public class ResponseAddressStringBuilder {
    private static final String ADDRESS_NOT_SPECIFIED = "Адрес не указан";
    private String region;
    private String district;
    private String city;
    private String microdistrict;
    private String street;
    private String building;
    private String housing;
    private String apartment;
    private String room;

    public String getTextAddress(AddressDTO addressDTO) {
        this.region = addressDTO.getRegion();
        this.district = addressDTO.getDistrict();
        this.city = addressDTO.getCity();
        this.microdistrict = addressDTO.getMicrodistrict();
        this.street = addressDTO.getStreet();
        this.building = addressDTO.getBuilding();
        this.housing = addressDTO.getHousing();
        this.apartment = addressDTO.getApartment();
        this.room = addressDTO.getRoom();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder = getTextRegion(stringBuilder);
        stringBuilder = getTextDistrict(stringBuilder);
        stringBuilder = getTextCity(stringBuilder);
        stringBuilder = getTextMicrodistrict(stringBuilder);
        stringBuilder = getTextStreet(stringBuilder);
        stringBuilder = getTextBuilding(stringBuilder);
        stringBuilder = getTextHousing(stringBuilder);
        stringBuilder = getTextApartment(stringBuilder);
        stringBuilder = getTextRoom(stringBuilder);

        return stringBuilder.length() != 0 ? stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(",")).toString()
                : ADDRESS_NOT_SPECIFIED;

    }

    private StringBuilder getTextRegion(StringBuilder stringBuilder) {
        if (region != null) {
            stringBuilder
                    .append("Регон: ")
                    .append(region)
                    .append(", ");
        }
        return stringBuilder;
    }

    private StringBuilder getTextDistrict(StringBuilder stringBuilder) {
        if (district != null) {
            stringBuilder
                    .append("район: ")
                    .append(district)
                    .append(", ");
        }
        return stringBuilder;
    }

    private StringBuilder getTextCity(StringBuilder stringBuilder) {
        if (city != null) {
            stringBuilder
                    .append("населенный пункт: ")
                    .append(city)
                    .append(", ");
        }
        return stringBuilder;
    }

    private StringBuilder getTextMicrodistrict(StringBuilder stringBuilder) {
        if (microdistrict != null) {
            stringBuilder
                    .append("микрорайон: ")
                    .append(microdistrict)
                    .append(", ");
        }
        return stringBuilder;
    }

    private StringBuilder getTextStreet(StringBuilder stringBuilder) {
        if (street != null) {
            stringBuilder
                    .append("улица/проспект: ")
                    .append(street)
                    .append(", ");
        }
        return stringBuilder;
    }

    private StringBuilder getTextBuilding(StringBuilder stringBuilder) {
        if (building != null) {
            stringBuilder
                    .append("дом: ")
                    .append(building)
                    .append(", ");
        }
        return stringBuilder;
    }

    private StringBuilder getTextHousing(StringBuilder stringBuilder) {
        if (housing != null) {
            stringBuilder
                    .append("корпус: ")
                    .append(housing)
                    .append(", ");
        }
        return stringBuilder;
    }

    private StringBuilder getTextApartment(StringBuilder stringBuilder) {
        if (apartment != null) {
            stringBuilder
                    .append("квартира: ")
                    .append(apartment)
                    .append(", ");
        }
        return stringBuilder;
    }

    private StringBuilder getTextRoom(StringBuilder stringBuilder) {
        if (room != null) {
            stringBuilder
                    .append("комната: ")
                    .append(room)
                    .append(", ");
        }
        return stringBuilder;
    }
}
