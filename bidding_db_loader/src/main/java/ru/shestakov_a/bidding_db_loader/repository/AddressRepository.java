package ru.shestakov_a.bidding_db_loader.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import real_estate.entity.AddressEntity;

/**
 * Интерфейс репозитория для сущности {@link AddressEntity}
 * @author Shestakov Artem
 * */
public interface AddressRepository extends JpaRepository<AddressEntity, Integer> {
}
