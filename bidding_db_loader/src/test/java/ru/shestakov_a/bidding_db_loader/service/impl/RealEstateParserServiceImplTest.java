package ru.shestakov_a.bidding_db_loader.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import real_estate.dto.controller.AddressDTO;
import real_estate.dto.controller.RealEstateDTO;
import real_estate.entity.AddressEntity;
import real_estate.entity.RealEstateEntity;
import real_estate.parser.RealEstateType;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RealEstateParserServiceImplTest {

    RealEstateParserServiceImpl realEstateParserService = new RealEstateParserServiceImpl();

    private final List<RealEstateDTO> realEstateDTOList = new ArrayList<>();

    @BeforeAll
    void setup() {
        realEstateDTOList.add(mapRealEstateEntityToDto(RealEstateEntity.builder()
                .id(1)
                .address(
                        AddressEntity.builder()
                                .region("вологодская")
                                .city("череповец")
                                .street("п. оникина")
                                .building("7")
                                .apartment("15")
                                .build()
                )
                .cadastralNumber("35:21:0302005:2204")
                .realEstateType(RealEstateType.ROOM.getValue())
                .link("https://127.0.0.1/test")
                .minPrice("235450.00")
                .publishDate(LocalDateTime.ofInstant(Instant.parse("2022-05-07T13:27:26.761Z"), ZoneId.ofOffset("UTC", ZoneOffset.of("+03"))))
                .biddingStartTime(LocalDateTime.ofInstant(Instant.parse("2022-05-08T06:00:00.000Z"), ZoneId.ofOffset("UTC", ZoneOffset.of("+03"))))
                .biddingEndTime(LocalDateTime.ofInstant(Instant.parse("2022-06-03T20:59:00.000Z"), ZoneId.ofOffset("UTC", ZoneOffset.of("+03"))))
                .auctionStartDate(LocalDateTime.ofInstant(Instant.parse("2022-06-08T08:00:00.000Z"), ZoneId.ofOffset("UTC", ZoneOffset.of("+03"))))
                .build()));

        realEstateDTOList.add(mapRealEstateEntityToDto(RealEstateEntity.builder()
                .id(2)
                .address(
                        AddressEntity.builder()
                                .region("вологодская")
                                .city("череповец")
                                .street("металлургов")
                                .building("27")
                                .apartment("4")
                                .room("3")
                                .build()
                )
                .cadastralNumber("35:21:0401013:2284")
                .realEstateType(RealEstateType.OWNERSHIP_SHARE.getValue())
                .link("https://127.0.0.1/test")
                .minPrice("66300.00")
                .publishDate(LocalDateTime.ofInstant(Instant.parse("2022-05-07T13:27:26.761Z"), ZoneId.ofOffset("UTC", ZoneOffset.of("+03"))))
                .biddingStartTime(LocalDateTime.ofInstant(Instant.parse("2022-05-08T06:00:00.000Z"), ZoneId.ofOffset("UTC", ZoneOffset.of("+03"))))
                .biddingEndTime(LocalDateTime.ofInstant(Instant.parse("2022-06-03T20:59:00.000Z"), ZoneId.ofOffset("UTC", ZoneOffset.of("+03"))))
                .auctionStartDate(LocalDateTime.ofInstant(Instant.parse("2022-06-08T08:00:00.000Z"), ZoneId.ofOffset("UTC", ZoneOffset.of("+03"))))
                .build()));
    }

    @ParameterizedTest
    @CsvSource({"target/classes/ForTest/exampleForTest.json"})
    void getRealEstateList(String directoryName) throws IOException {
        Path path = Path.of(directoryName).toAbsolutePath();
        List<RealEstateEntity> var1 = realEstateParserService.getRealEstateList(path.toString());
        List<RealEstateDTO> actual = var1.stream().map(this::mapRealEstateEntityToDto).collect(Collectors.toList());

        Assertions.assertEquals(realEstateDTOList, actual);
    }

    private AddressDTO mapAddressEntityToDto(AddressEntity address) {
        return AddressDTO.builder()
                .region(address.getRegion())
                .district(address.getDistrict())
                .city(address.getCity())
                .microdistrict(address.getMicrodistrict())
                .street(address.getStreet())
                .building(address.getBuilding())
                .apartment(address.getApartment())
                .room(address.getRoom())
                .housing(address.getHousing())
                .build();
    }

    private RealEstateDTO mapRealEstateEntityToDto(RealEstateEntity realEstateEntity) {
        return RealEstateDTO.builder()
                .id(realEstateEntity.getId().toString())
                .realEstateType(realEstateEntity.getRealEstateType())
                .cadastralNumber(realEstateEntity.getCadastralNumber())
                .link(realEstateEntity.getLink())
                .address(
                        mapAddressEntityToDto(realEstateEntity.getAddress()))
                .minPrice(realEstateEntity.getMinPrice())
                .publishDate(realEstateEntity.getPublishDate())
                .biddingStartTime(realEstateEntity.getBiddingStartTime())
                .biddingEndTime(realEstateEntity.getBiddingEndTime())
                .auctionStartDate(realEstateEntity.getAuctionStartDate())
                .build();
    }
}