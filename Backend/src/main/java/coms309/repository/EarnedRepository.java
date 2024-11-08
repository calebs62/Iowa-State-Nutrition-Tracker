package coms309.repository;

import coms309.entity.Earned;
import coms309.entity.EarnedKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EarnedRepository extends JpaRepository<Earned, EarnedKey> {
}
