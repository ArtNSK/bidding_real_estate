package ru.shestakov_a.bidding_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import real_estate.dto.controller.*;
import real_estate.entity.AddressEntity;
import real_estate.entity.RealEstateEntity;
import ru.shestakov_a.bidding_app.exception.EntityNotFoundException;
import ru.shestakov_a.bidding_app.repository.RealEstateRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Класс реализации интерфейса сервисного слоя
 * @author Shestakov Artem
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class RealEstateServiceImpl implements RealEstateService {
    private static final String CURSOR_INIT = "0";

    private final RealEstateRepository realEstateRepository;

    /**
     * Метода сохраняет сущности объектов недвижимости {@link RealEstateEntity} в базу данных
     * @param realEstateEntityList
     * */
    @Override
    public void saveAllRealEstate(List<RealEstateEntity> realEstateEntityList) {
        realEstateRepository.saveAll(realEstateEntityList);
    }

    /**
     * @param realEstateId
     * @return {@link RealEstateDTO} полученный из сущности по id
     * */
    @Override
    public RealEstateDTO getRealEstateById(String realEstateId) {
        Integer id = Integer.parseInt(realEstateId);
        Optional<RealEstateEntity> optionalRealEstate = realEstateRepository.findById(id);
        return optionalRealEstate.map(realEstateEntity ->
                RealEstateDTO.builder()
                        .id(realEstateEntity.getId().toString())
                        .address(
                                mapAddressEntityToDto(realEstateEntity.getAddress()))
                        .cadastralNumber(realEstateEntity.getCadastralNumber())
                        .realEstateType(realEstateEntity.getRealEstateType())
                        .link(realEstateEntity.getLink())
                        .minPrice(realEstateEntity.getMinPrice())
                        .publishDate(realEstateEntity.getPublishDate())
                        .biddingStartTime(realEstateEntity.getBiddingStartTime())
                        .biddingEndTime(realEstateEntity.getBiddingEndTime())
                        .auctionStartDate(realEstateEntity.getAuctionStartDate())
                        .build())
                .orElseThrow(EntityNotFoundException::new);
    }

    /**
     * Метод возвращает список всех объектов недвижимости с использованием пагинации.
     * При указании кадастрового номера, метод возвращает список всех объектов недвижимости
     * с заданным кадастровым номером.
     * @param limit Ограничение на количество объектов в ответе (required)
     * @param cursor Курсор, для получения следующей части ответа (optional)
     * @param cadastralNumber Кадастровый номер, для поиска объектов недвижимости (optional)
     * @return {@link RealEstateResponseDTO} в котором содержимтся список {@link RealEstateDTO}
     * объектов недвижимости
     * */
    @Override
    public RealEstateResponseDTO<RealEstateDTO> getRealEstateList(
            int limit,
            Optional<String> cursor,
            Optional<String> cadastralNumber) {
        if (cadastralNumber.isEmpty()) {
            return getAllCadastralNumberList(limit, cursor);
        } else {
            return getCadastralNumberListByCadastralNumber(limit, cursor, cadastralNumber.get());
        }
    }

    /**
     * Метод возвращает список всех объектов недвижимости с использованием пагинации.
     * @param limit Ограничение на количество объектов в ответе (required)
     * @param cursor Курсор, для получения следующей части ответа (optional)
     * @return {@link RealEstateResponseDTO} в котором содержимтся список {@link RealEstateDTO}
     * объектов недвижимости
     * */
    public RealEstateResponseDTO<RealEstateDTO> getAllCadastralNumberList(int limit, Optional<String> cursor) {
        Integer cursorInt = Integer.parseInt(cursor.orElse(CURSOR_INIT));
        List<RealEstateDTO> cadastralNumbers =
                mapRealEstateEntityToDto(
                        realEstateRepository.findByIdAfterOrderById(cursorInt, PageRequest.of(0, limit))
                );
        isEmptyList(cadastralNumbers);
        return new RealEstateResponseDTO<>(cadastralNumbers,
                getNewCursor(cursorInt, cadastralNumbers, RealEstateDTO::getId));
    }

    /**
     * Метод возвращает список всех объектов недвижимости с заданным кадастровым номером.
     * @param limit Ограничение на количество объектов в ответе (required)
     * @param cursor Курсор, для получения следующей части ответа (optional)
     * @param cadastralNumber Кадастровый номер, для поиска объектов недвижимости (required)
     * @return {@link RealEstateResponseDTO} в котором содержимтся список {@link RealEstateDTO}
     * объектов недвижимости
     * */
    public RealEstateResponseDTO<RealEstateDTO> getCadastralNumberListByCadastralNumber(
            int limit,
            Optional<String> cursor,
            String cadastralNumber
    ) {
        Integer cursorInt = Integer.parseInt(cursor.orElse(CURSOR_INIT));
        List<RealEstateDTO> cadastralNumbers =
                mapRealEstateEntityToDto(
                        realEstateRepository.findByIdGreaterThanAndCadastralNumberContainingOrderById(
                                cursorInt, cadastralNumber, PageRequest.of(0, limit))
                );
        isEmptyList(cadastralNumbers);
        return new RealEstateResponseDTO<>(cadastralNumbers,
                getNewCursor(cursorInt, cadastralNumbers, RealEstateDTO::getId));
    }

    /**
     * Метод возвращает список всех объектов недвижимости с заданными кадастровыми номерами.
     * @param limit Ограничение на количество объектов в ответе (required)
     * @param cursor Курсор, для получения следующей части ответа (optional)
     * @param cadastralNumberList Список кадастровых номеров
     * @return {@link RealEstateResponseDTO} в котором содержимтся список {@link RealEstateDTO}
     * объектов недвижимости
     * */
    @Override
    public RealEstateResponseDTO<RealEstateDTO> getRealEstateListByCadastralNumbers(
            int limit,
            Optional<String> cursor,
            List<String> cadastralNumberList) {

        Integer cursorInt = Integer.parseInt(cursor.orElse(CURSOR_INIT));
        List<RealEstateDTO> cadastralNumbers = new ArrayList<>();
        int limitTemp = 0;

        for (int i = 0; i < cadastralNumberList.size() && limitTemp < limit; i++) {
            List<RealEstateDTO> tempList =
                    mapRealEstateEntityToDto(
                            realEstateRepository.findByIdGreaterThanAndCadastralNumberContainingOrderById(
                                    cursorInt, cadastralNumberList.get(i), PageRequest.of(0, limit - limitTemp))
                    );

            cursorInt = Integer.parseInt(getNewCursor(cursorInt, tempList, RealEstateDTO::getId));
            limitTemp += tempList.size();
            cadastralNumbers.addAll(tempList);
        }
        isEmptyList(cadastralNumbers);
        return new RealEstateResponseDTO<>(cadastralNumbers,
                getNewCursor(cursorInt, cadastralNumbers, RealEstateDTO::getId));
    }

    /**
     * Метод возвращает список всех объектов недвижимости по заданному региону и городу
     * @param limit Ограничение на количество объектов в ответе (required)
     * @param cursor Курсор, для получения следующей части ответа (optional)
     * @param region
     * @param city
     * @return {@link RealEstateResponseDTO} в котором содержимтся список {@link RealEstateDTO}
     * объектов недвижимости
     * */
    @Override
    public RealEstateResponseDTO<RealEstateDTO> getRealEstateListByCity(int limit, Optional<String> cursor, String region, String city) {
        Integer cursorInt = Integer.parseInt(cursor.orElse(CURSOR_INIT));
        List<RealEstateDTO> cadastralNumbers = mapRealEstateEntityToDto(
                realEstateRepository.findByIdGreaterThanAndAddress_RegionAndAddress_CityOrderById(cursorInt, region, city, PageRequest.of(0, limit))
        );
        isEmptyList(cadastralNumbers);
        return new RealEstateResponseDTO<>(cadastralNumbers,
                getNewCursor(cursorInt, cadastralNumbers, RealEstateDTO::getId));
    }

    /**
     * Метод возвращает список всех объектов недвижимости по заданному региону
     * @param limit Ограничение на количество объектов в ответе (required)
     * @param cursor Курсор, для получения следующей части ответа (optional)
     * @param region
     * @return {@link RealEstateResponseDTO} в котором содержимтся список {@link RealEstateDTO}
     * объектов недвижимости
     * */
    @Override
    public RealEstateResponseDTO<RealEstateDTO> getRealEstateListByRegion(int limit, Optional<String> cursor, String region) {
        Integer cursorInt = Integer.parseInt(cursor.orElse(CURSOR_INIT));
        List<RealEstateDTO> cadastralNumbers = mapRealEstateEntityToDto(
                realEstateRepository.findByIdGreaterThanAndAddress_RegionOrderById(cursorInt, region, PageRequest.of(0, limit))
        );
        isEmptyList(cadastralNumbers);
        return new RealEstateResponseDTO<>(cadastralNumbers,
                getNewCursor(cursorInt, cadastralNumbers, RealEstateDTO::getId));
    }

    /**
     * @param cadastralNumber
     * @return {@link QuantityRealEstateCadastralNumberDTO} в которм указано количество сущностей с данным кадастровым номером
     * */
    @Override
    public QuantityRealEstateCadastralNumberDTO getRealEstateQuantityByCadastralNumber(String cadastralNumber) {
        QuantityRealEstateCadastralNumberDTO quantityRealEstateCadastralNumberDTO = new QuantityRealEstateCadastralNumberDTO();
        quantityRealEstateCadastralNumberDTO.setCadastralNumber(cadastralNumber);
        int quantity = realEstateRepository.countByCadastralNumber(cadastralNumber);
        quantityRealEstateCadastralNumberDTO.setQuantity(quantity);
        return quantityRealEstateCadastralNumberDTO;
    }

    /**
     * @param region
     * @param city
     * @return {@link QuantityRealEstateCityDTO} в котором указано количсетво сущностей по заданному городу и региону
     * */
    @Override
    public QuantityRealEstateCityDTO getRealEstateQuantityByCity(String region, String city) {
        QuantityRealEstateCityDTO quantityRealEstateCityDTO = new QuantityRealEstateCityDTO();
        quantityRealEstateCityDTO.setCity(city);
        quantityRealEstateCityDTO.setRegion(region);
        int quantity = realEstateRepository.countByAddressRegionAndAddress_City(region, city);
        quantityRealEstateCityDTO.setQuantity(quantity);
        return quantityRealEstateCityDTO;
    }

    /**
     * @param region
     * @return {@link QuantityRealEstateRegionDTO} в котором указано количсетво сущностей по заданному региону
     * */
    @Override
    public QuantityRealEstateRegionDTO getRealEstateQuantityByRegion(String region) {
        QuantityRealEstateRegionDTO quantityRealEstateRegionDTO = new QuantityRealEstateRegionDTO();
        quantityRealEstateRegionDTO.setRegion(region);
        int quantity = realEstateRepository.countByAddressRegion(region);
        quantityRealEstateRegionDTO.setQuantity(quantity);
        return quantityRealEstateRegionDTO;
    }

    /**
     * @return {@link QuantityDTO} в котором указано общее количсетво сущностей
     * */
    @Override
    public QuantityDTO getRealEstateAllQuantity() {
        int quantity = (int) realEstateRepository.count();
        return new QuantityDTO(quantity);
    }

    /**
     * Метод удаляет сущности объектов недвижимости дата проведения аукциона по которым раньше передаваемой даты
     * @param date
     * */
    @Override
    @Transactional
    public void deleteOldRealEstate(LocalDateTime date) {
        realEstateRepository.deleteAllByAuctionStartDateBefore(date);
    }

    /**
     * Метод возвращает значение курсора для последующей пагинации
     * @param currentCursor Текущее значение курсора
     * @param items Список объектов
     * @param getId id объекта
     * @return Возвращает id последнего объекта из списка в качестве нового значения курсора, либо текущий курсор
     * */
    private <T> String getNewCursor(Integer currentCursor, List<T> items, Function<T, String> getId) {
        return items.size() > 0 ? getId.apply(items.get(items.size() - 1)) : currentCursor.toString();
    }

    /**
     * Метод преобразует сущности в DTO
     * @param realEstateEntityList Список {@link RealEstateEntity}
     * @return Список {@link RealEstateDTO}
     * */
    private List<RealEstateDTO> mapRealEstateEntityToDto(List<RealEstateEntity> realEstateEntityList) {
        return realEstateEntityList.stream().map(realEstateEntity ->
                RealEstateDTO.builder()
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
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Метод преобразует сущность в DTO
     * @param address {@link AddressEntity}
     * @return {@link AddressDTO}
     * */
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

    /**
     * Метод проверяет наличие элементов в списке
     * @throws EntityNotFoundException
     * */
    private void isEmptyList(List<RealEstateDTO> list) {
        if (list.size() == 0) {
            throw new EntityNotFoundException();
        }
    }
}