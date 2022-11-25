package real_estate.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "real_estate")
public class RealEstateEntity implements Serializable {
    @Id
    @Column(name = "id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "real_estate_idreal_estate_seq",
            sequenceName = "real_estate_idreal_estate_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "real_estate_idreal_estate_seq")
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    @Column(name = "cadastral_number")
    private String cadastralNumber;

    @Column(name = "link")
    private String link;

    @Column(name = "min_price")
    private String minPrice;

    @Column(name = "real_estate_type")
    private String realEstateType;

    @Column(name = "lot_number")
    private Integer lotNumber;

    @Column(name = "lot_name", columnDefinition = "text")
    private String lotName;

    @Column(name = "publish_date")
    private LocalDateTime publishDate;

    @Column(name = "bidding_start_time")
    private LocalDateTime biddingStartTime;

    @Column(name = "bidding_end_time")
    private LocalDateTime biddingEndTime;

    @Column(name = "auction_start_date")
    private LocalDateTime auctionStartDate;

    /*@Column(name = "address", columnDefinition = "text")
    private String addressReal;*/
}