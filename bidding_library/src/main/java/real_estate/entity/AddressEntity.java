package real_estate.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "address")
public class AddressEntity {
    @Id
    @Column(name = "id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "address_idaddress_seq",
            sequenceName = "address_idaddress_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "address_idaddress_seq")
    private Integer id;

    @Column(name = "region")
    private String region;

    @Column(name = "district")
    private String district;

    @Column(name = "city")
    private String city;

    @Column(name = "microdistrict")
    private String microdistrict;

    @Column(name = "street")
    private String street;

    @Column(name = "building")
    private String building;

    @Column(name = "apatrment")
    private String apartment;

    @Column(name = "room")
    private String room;

    @Column(name = "housing")
    private String housing;
}