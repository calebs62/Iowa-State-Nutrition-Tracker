package coms309.repository;

import coms309.entity.FoodEaten;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FoodEatenRepository extends JpaRepository<FoodEaten, Integer> {
    List<FoodEaten> findAllByDateBetween(LocalDateTime dateStart, LocalDateTime dateEnd);
}
