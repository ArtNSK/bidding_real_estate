package real_estate.dto.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RealEstateTestingDTO {
    private Integer limit;
    private String cursor;
    private List<String> cadastralNumbers;
}
