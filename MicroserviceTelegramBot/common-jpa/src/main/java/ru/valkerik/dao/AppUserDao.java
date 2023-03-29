package ru.valkerik.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.valkerik.entity.AppUser;

import java.util.Optional;

public interface AppUserDao extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByTelegramUserId(Long id);
    Optional<AppUser> findById(Long id);
    Optional<AppUser> findByEmail(String email);

}
