package ru.shestakov_a.bidding_db_loader.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import real_estate.entity.RealEstateEntity;

/**
 * Интерфейс репозитория для сущности {@link RealEstateEntity}
 * @author Shestakov Artem
 * */
@Repository
public interface RealEstateRepository extends JpaRepository<RealEstateEntity, Integer> {
}


