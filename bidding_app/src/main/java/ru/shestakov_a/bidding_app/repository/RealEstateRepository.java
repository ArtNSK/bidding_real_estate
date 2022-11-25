package ru.shestakov_a.bidding_app.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import real_estate.entity.RealEstateEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс репозитория для сущности {@link RealEstateEntity}
 * @author Shestakov Artem
 * */
@Repository
public interface RealEstateRepository extends JpaRepository<RealEstateEntity, Integer> {

    /**
     * @param id - id сущности
     * @param pageable - интерфейс пагинации
     * @return Список сущностей объектов недвижимости с использованием пагинации
     * */
    List<RealEstateEntity> findByIdAfterOrderById(Integer id, Pageable pageable);

    /**
     * @param id - id сущности
     * @param pageable - интерфейс пагинации
     * @param cadastralNumber - кадастровый номер
     * @return Список сущностей объектов недвижимости, выбранных по кадастровому номеру с использованием пагинации
     * */
    List<RealEstateEntity> findByIdGreaterThanAndCadastralNumberContainingOrderById(Integer id, String cadastralNumber, Pageable pageable);

    /**
     * @param id - id сущности
     * @param pageable - интерфейс пагинации
     * @param region
     * @param city
     * @return Список сущностей объектов недвижимости, выбранных по городу и региону с использованием пагинации
     * */
    List<RealEstateEntity> findByIdGreaterThanAndAddress_RegionAndAddress_CityOrderById(Integer id, String region, String city, Pageable pageable);

    /**
     * @param id - id сущности
     * @param pageable - интерфейс пагинации
     * @param region
     * @return Список сущностей объектов недвижимости, выбранных по региону с использованием пагинации
     * */
    List<RealEstateEntity> findByIdGreaterThanAndAddress_RegionOrderById(Integer id, String region, Pageable pageable);

    /**
     * @param cadastralNumber
     * @return Количество сущностей объектов недвижимости с запрашиваемым кадастровым номероа
     * */
    int countByCadastralNumber(String cadastralNumber);

    /**
     * @param region
     * @param city
     * @return Количество сущнсотей объектов недвижимости с запрашиваемым городом и регионом
     * */
    int countByAddressRegionAndAddress_City(String region, String city);

    /**
     * @param region
     * @return Количество сущностей объектов недвижимости по региону
     * */
    int countByAddressRegion(String region);

    /**
     * Метод удалеяет сущности объеков недвижимости из базы данных, дата начала аукциона которых,
     * раньше передаваемой
    * @param date
    * */
    void deleteAllByAuctionStartDateBefore(LocalDateTime date);
}