package real_estate.dto.parser;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class LotDatesDTO {
    private LocalDateTime publishDate;
    private LocalDateTime biddingStartTime;
    private LocalDateTime biddingEndTime;
    private LocalDateTime auctionStartDate;

}
