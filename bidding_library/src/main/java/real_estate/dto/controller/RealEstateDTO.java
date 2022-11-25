package real_estate.dto.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class RealEstateDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("cadastralNumber")
    private String cadastralNumber;

    @JsonProperty("link")
    private String link;

    @JsonProperty("address")
    private AddressDTO address;

    @JsonProperty("minPrice")
    private String minPrice;

    @JsonProperty("realEstateType")
    private String realEstateType;

    @JsonProperty("publishDate")
    private LocalDateTime publishDate;

    @JsonProperty("biddingStartTime")
    private LocalDateTime biddingStartTime;

    @JsonProperty("biddingEndTime")
    private LocalDateTime biddingEndTime;

    @JsonProperty("auctionStartDate")
    private LocalDateTime auctionStartDate;

}
