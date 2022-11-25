package real_estate.dto.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class QuantityDTO {
    @JsonProperty("quantity")
    private int quantity;
}
