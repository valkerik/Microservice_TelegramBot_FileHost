package ru.valkerik.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.valkerik.entity.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
