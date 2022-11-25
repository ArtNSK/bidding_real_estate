package real_estate.dto.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CadastralNumberDTO {
    @JsonProperty("cadastralNumbers")
    private List<String> cadastralNumbers;

    @NonNull
    public List<String> getCadastralNumbers() {
        return cadastralNumbers;
    }
}