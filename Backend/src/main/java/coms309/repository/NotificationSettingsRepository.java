package coms309.repository;

import coms309.entity.NotificationSettings;
import coms309.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Integer> {
    Optional<NotificationSettings> findByUserUidOrUserOrUserId(int userUid, int user, int userId);
}
