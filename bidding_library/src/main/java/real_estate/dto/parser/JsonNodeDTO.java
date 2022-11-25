package real_estate.dto.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class JsonNodeDTO {
//    private JsonNode rootNode;
    private JsonNode commonInfo;
    private JsonNode biddCondition;
    private JsonNode biddType;
}
