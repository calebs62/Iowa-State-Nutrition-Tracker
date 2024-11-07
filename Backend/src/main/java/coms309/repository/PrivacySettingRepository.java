package coms309.repository;

import coms309.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivacySettingRepository extends JpaRepository<PrivacySettings, Integer> {

}
