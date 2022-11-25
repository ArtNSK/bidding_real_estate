package real_estate.dto.parser;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LotAddressDTO {
    private String region;
    private String city;
    private String address;
}
