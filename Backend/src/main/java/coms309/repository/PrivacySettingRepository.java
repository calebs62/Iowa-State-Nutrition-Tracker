package coms309.repository;

import coms309.entity.PrivacySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivacySettingRepository extends JpaRepository<PrivacySettings, Integer> {

}
