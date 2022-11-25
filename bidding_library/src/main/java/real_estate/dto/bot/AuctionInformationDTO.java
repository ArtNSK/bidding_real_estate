package real_estate.dto.bot;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class AuctionInformationDTO  {
    private LocalDateTime biddingStartTime;
    private LocalDateTime biddingEndTime;
    private LocalDateTime auctionStartDate;
}