package pro.sky.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.entity.AppUser;

import java.util.Optional;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByTelegramUserId(Long telegramUserId);
    Optional<AppUser> findById(Long id);
    Optional<AppUser> findByEmail(String email);
}
