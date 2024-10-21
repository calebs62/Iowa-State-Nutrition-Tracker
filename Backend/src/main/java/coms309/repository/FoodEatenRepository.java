package coms309.repository;

import coms309.entity.FoodEaten;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodEatenRepository extends JpaRepository<FoodEaten, Integer> {
}
