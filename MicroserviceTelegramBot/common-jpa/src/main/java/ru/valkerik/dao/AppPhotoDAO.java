package ru.valkerik.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.valkerik.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
