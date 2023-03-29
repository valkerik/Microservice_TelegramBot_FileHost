package ru.valkerik.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.valkerik.entity.RawData;

public interface RawDataDAO extends JpaRepository<RawData, Long> {
}
