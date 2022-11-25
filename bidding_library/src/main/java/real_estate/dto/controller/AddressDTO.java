package real_estate.dto.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class AddressDTO {
    @JsonProperty("region")
    private String region;

    @JsonProperty("district")
    private String district;

    @JsonProperty("city")
    private String city;

    @JsonProperty("microdistrict")
    private String microdistrict;

    @JsonProperty("street")
    private String street;

    @JsonProperty("building")
    private String building;

    @JsonProperty("apatrment")
    private String apartment;

    @JsonProperty("room")
    private String room;

    @JsonProperty("housing")
    private String housing;
}
