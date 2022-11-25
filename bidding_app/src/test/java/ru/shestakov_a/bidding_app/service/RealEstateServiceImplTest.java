package ru.shestakov_a.bidding_app.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import real_estate.dto.controller.*;
import real_estate.dto.test.RealEstateTestingDTO;
import real_estate.entity.AddressEntity;
import real_estate.entity.RealEstateEntity;
import ru.shestakov_a.bidding_app.repository.RealEstateRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RealEstateServiceImplTest {

    @Mock
    RealEstateRepository realEstateRepository;
    @InjectMocks
    RealEstateServiceImpl realEstateService;

    private final List<RealEstateEntity> realEstateEntityList = new ArrayList<>();
    private final List<RealEstateDTO> realEstateDTOS = new ArrayList<>();

    @BeforeAll
    void setup() {
        MockitoAnnotations.openMocks(this);
        for (int i = 0; i < 10; i++) {
            AddressEntity address = AddressEntity.builder()
                    .id(i)
                    .region("California")
                    .district("Pacoima")
                    .city("Los Angeles")
                    .microdistrict("")
                    .street("Roslyndale Avenue")
                    .building("9303")
                    .apartment(String.valueOf(1885 + i))
                    .room(String.valueOf(2015 + i))
                    .housing(String.valueOf(1985 + i))
                    .build();

            RealEstateEntity realEstateEntity = RealEstateEntity.builder()
                    .id(i)
                    .address(address)
                    .cadastralNumber("00:00:0000000:0" + i)
                    .link("http://127.0.0.1")
                    .minPrice("1")
                    .realEstateType("Комната")
                    .lotNumber(0)
                    .lotName("Комната в доме")
                    .publishDate(LocalDateTime.of(1985, Month.OCTOBER, 26, 1, 21))
                    .biddingStartTime(LocalDateTime.of(1885, Month.SEPTEMBER, 2, 8, 0))
                    .biddingEndTime(LocalDateTime.of(2015, Month.OCTOBER, 21, 7, 28))
                    .auctionStartDate(LocalDateTime.of(2015, Month.OCTOBER, 21, 7, 28))
                    .build();
            realEstateEntityList.add(realEstateEntity);
            realEstateDTOS.add(
                    mapRealEstateEntityToDto(realEstateEntity)
            );
        }
    }

    @ParameterizedTest
    @CsvSource({"1"})
    void getRealEstateById(String realEstateId) {
        when(realEstateRepository.findById(anyInt()))
                .thenReturn(realEstateEntityList.stream()
                        .filter(realEstateEntity -> realEstateEntity.getId().equals(Integer.parseInt(realEstateId)))
                        .findFirst());
        RealEstateDTO expected = realEstateDTOS.stream()
                .filter(dto ->
                        dto.getId().equals(realEstateId))
                .findFirst().get();
        RealEstateDTO actual = realEstateService.getRealEstateById(realEstateId);

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource({"1,0"})
    void getAllCadastralNumberList(int limit,
                                   String cursor) {
        when(realEstateRepository.findByIdAfterOrderById(anyInt(), any(Pageable.class)))
                .thenReturn(realEstateEntityList.stream()
                        .skip(Long.parseLong(cursor)).limit(limit).collect(Collectors.toList()));

        List<RealEstateDTO> dtos = realEstateDTOS.stream()
                .skip(Long.parseLong(cursor)).limit(limit).collect(Collectors.toList());

        RealEstateResponseDTO<RealEstateDTO> expected = new RealEstateResponseDTO<>(dtos, dtos.get(dtos.size() - 1).getId());
        RealEstateResponseDTO<RealEstateDTO> actual = realEstateService.getAllCadastralNumberList(limit, Optional.of(cursor));

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource({"1,0,00:00:0000000:01"})
    void getCadastralNumberListByCadastralNumber(int limit,
                                                 String cursor,
                                                 String cadastralNumber) {
        when(realEstateRepository.findByIdGreaterThanAndCadastralNumberContainingOrderById(anyInt(), anyString(), any(Pageable.class)))
                .thenReturn(realEstateEntityList.stream()
                        .skip(Long.parseLong(cursor))
                        .filter(
                                entity -> entity.getCadastralNumber().equals(cadastralNumber))
                        .limit(limit)
                        .collect(Collectors.toList()));

        List<RealEstateDTO> dtos = realEstateDTOS.stream()
                .skip(Long.parseLong(cursor))
                .filter(
                        entity -> entity.getCadastralNumber().equals(cadastralNumber))
                .limit(limit)
                .collect(Collectors.toList());

        RealEstateResponseDTO<RealEstateDTO> expected = new RealEstateResponseDTO<>(dtos, dtos.get(dtos.size() - 1).getId());
        RealEstateResponseDTO<RealEstateDTO> actual = realEstateService.getCadastralNumberListByCadastralNumber(limit,
                Optional.of(cursor),
                cadastralNumber);

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ArgumentsSource(RealEstateArgumentProvider.class)
    void getRealEstateListByCadastralNumbers(RealEstateTestingDTO realEstateTestingDTO) {
        Integer limit = realEstateTestingDTO.getLimit();
        String cursor = realEstateTestingDTO.getCursor();
        List<String> cadastralNumberList = realEstateTestingDTO.getCadastralNumbers();
        int limitTemp = 0;
        List<RealEstateDTO> dtos = new ArrayList<>();
        for (int i = 0; i < cadastralNumberList.size() && limitTemp < limit; i++) {
            String number = cadastralNumberList.get(i);
            when(realEstateRepository.findByIdGreaterThanAndCadastralNumberContainingOrderById(anyInt(), eq(number), any(Pageable.class)))
                    .thenReturn(realEstateEntityList.stream()
                            .skip(Long.parseLong(cursor))
                            .filter(
                                    entity -> entity.getCadastralNumber().equals(number))
                            .limit(limit - limitTemp)
                            .collect(Collectors.toList()));
            List<RealEstateDTO> tempList = realEstateDTOS.stream()
                    .skip(Long.parseLong(cursor))
                    .filter(
                            entity -> entity.getCadastralNumber().equals(number))
                    .limit(limit - limitTemp)
                    .collect(Collectors.toList());
            dtos.addAll(tempList);
            limitTemp += tempList.size();
        }

        RealEstateResponseDTO<RealEstateDTO> expected = new RealEstateResponseDTO<>(dtos, dtos.get(dtos.size() - 1).getId());
        RealEstateResponseDTO<RealEstateDTO> actual = realEstateService
                .getRealEstateListByCadastralNumbers(
                        limit,
                        Optional.of(cursor),
                        cadastralNumberList);

        Assertions.assertEquals(expected, actual);
    }

    static class RealEstateArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(Arguments.of(new RealEstateTestingDTO(1,
                    "0",
                    Stream.of("00:00:0000000:00",
                                    "00:00:0000000:01",
                                    "00:00:0000000:02")
                            .collect(Collectors.toList()))));
        }
    }

    @ParameterizedTest
    @CsvSource({"3,0,California,Los Angeles"})
    void getRealEstateListByCity(int limit, String cursor, String region, String city) {
        when(realEstateRepository.findByIdGreaterThanAndAddress_RegionAndAddress_CityOrderById(
                anyInt(), anyString(), anyString(), any(Pageable.class)))
                .thenReturn(realEstateEntityList.stream()
                        .skip(Long.parseLong(cursor))
                        .filter(
                                entity -> entity.getAddress().getRegion().equals(region) &&
                                        entity.getAddress().getCity().equals(city))
                        .limit(limit)
                        .collect(Collectors.toList()));

        List<RealEstateDTO> dtos = realEstateDTOS.stream()
                .skip(Long.parseLong(cursor))
                .filter(
                        entity -> entity.getAddress().getRegion().equals(region) &&
                                entity.getAddress().getCity().equals(city))
                .limit(limit)
                .collect(Collectors.toList());

        RealEstateResponseDTO<RealEstateDTO> expected = new RealEstateResponseDTO<>(
                dtos, dtos.get(dtos.size() - 1).getId());
        RealEstateResponseDTO<RealEstateDTO> actual = realEstateService.getRealEstateListByCity(
                limit,
                Optional.of(cursor),
                region,
                city
        );

        Assertions.assertEquals(expected,actual);
    }

    @ParameterizedTest
    @CsvSource({"3,0,California"})
    void getRealEstateListByRegion(int limit, String cursor, String region) {
        when(realEstateRepository.findByIdGreaterThanAndAddress_RegionOrderById(
                anyInt(), anyString(),  any(Pageable.class)))
                .thenReturn(realEstateEntityList.stream()
                        .skip(Long.parseLong(cursor))
                        .filter(
                                entity -> entity.getAddress().getRegion().equals(region))
                        .limit(limit)
                        .collect(Collectors.toList()));

        List<RealEstateDTO> dtos = realEstateDTOS.stream()
                .skip(Long.parseLong(cursor))
                .filter(
                        entity -> entity.getAddress().getRegion().equals(region))
                .limit(limit)
                .collect(Collectors.toList());

        RealEstateResponseDTO<RealEstateDTO> expected = new RealEstateResponseDTO<>(
                dtos, dtos.get(dtos.size() - 1).getId());
        RealEstateResponseDTO<RealEstateDTO> actual = realEstateService.getRealEstateListByRegion(
                limit,
                Optional.of(cursor),
                region
        );

        Assertions.assertEquals(expected,actual);
    }

    @ParameterizedTest
    @CsvSource({"00:00:0000000:00"})
    void getRealEstateQuantityByCadastralNumber(String cadastralNumber) {
        when(realEstateRepository.countByCadastralNumber(cadastralNumber))
                .thenReturn((int) realEstateEntityList.stream()
                        .filter(entity ->
                                entity.getCadastralNumber().equals(cadastralNumber))
                        .count());

        int quantity = (int) realEstateDTOS.stream()
                .filter(dto ->
                        dto.getCadastralNumber().equals(cadastralNumber))
                .count();

        QuantityRealEstateCadastralNumberDTO expected = new QuantityRealEstateCadastralNumberDTO(cadastralNumber, quantity);
        QuantityRealEstateCadastralNumberDTO actual = realEstateService.getRealEstateQuantityByCadastralNumber(cadastralNumber);

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource({"California,Los Angeles"})
    void getRealEstateQuantityByCity(String region, String city) {
        when(realEstateRepository.countByAddressRegionAndAddress_City(region, city))
                .thenReturn((int) realEstateEntityList.stream()
                        .filter(entity ->
                                entity.getAddress().getRegion().equals(region)
                                && entity.getAddress().getCity().equals(city)
                        )
                        .count());

        int quantity = (int) realEstateDTOS.stream()
                .filter(dto ->
                        dto.getAddress().getRegion().equals(region)
                                && dto.getAddress().getCity().equals(city))
                .count();

        QuantityRealEstateCityDTO expected = new QuantityRealEstateCityDTO(region, city, quantity);
        QuantityRealEstateCityDTO actual = realEstateService.getRealEstateQuantityByCity(region, city);

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource({"California"})
    void getRealEstateQuantityByRegion(String region) {
        when(realEstateRepository.countByAddressRegion(region))
                .thenReturn((int) realEstateEntityList.stream()
                        .filter(entity ->
                                entity.getAddress().getRegion().equals(region)
                        )
                        .count());

        int quantity = (int) realEstateDTOS.stream()
                .filter(dto ->
                        dto.getAddress().getRegion().equals(region))
                .count();

        QuantityRealEstateRegionDTO expected = new QuantityRealEstateRegionDTO(region, quantity);
        QuantityRealEstateRegionDTO actual = realEstateService.getRealEstateQuantityByRegion(region);

        Assertions.assertEquals(expected, actual);
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