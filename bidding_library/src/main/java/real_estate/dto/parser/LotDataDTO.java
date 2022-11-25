package real_estate.dto.parser;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LotDataDTO {
    private String link;
    private int lotNumber;
    private String lotName;
    private String priceMin;
    private String realEstateType;
}
