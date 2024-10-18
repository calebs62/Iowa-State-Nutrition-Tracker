package coms309.repository;

import coms309.entity.FoodPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodPlanRepository extends JpaRepository<FoodPlan, Integer> {
}
