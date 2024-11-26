package coms309.repository;

import coms309.entity.FoodEaten;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FoodEatenRepository extends JpaRepository<FoodEaten, Integer> {
    List<FoodEaten> findAllByDateBetween(Date dateStart, Date dateEnd);
}
