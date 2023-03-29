package ru.valkerik.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.valkerik.entity.BinaryContent;

public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
