package coms309.controller;

import coms309.entity.FoodPlan;
import coms309.repository.FoodPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class FoodPlanController {
    @Autowired
    FoodPlanRepository planRepo;

    // Create
    @PostMapping("/plan")
    public FoodPlan savePlan(@RequestBody FoodPlan plan){
        return planRepo.save(plan);
    }

    // Read
    @GetMapping("/plan/{id}")
    public FoodPlan getPlanById(@PathVariable int id){
        return planRepo.findById(id).orElse(null);
    }

    // Update
    @PostMapping("/plan/update/{id}")
    public FoodPlan updatePlan(@PathVariable int id, @RequestBody Map<String, Object> newPlan){
        FoodPlan currPlan = planRepo.findById(id).orElse(null);
        if (currPlan != null){
            if (newPlan.containsKey("name")){
                currPlan.setName((String) newPlan.get("name"));
            }
            if (newPlan.containsKey("calories")){
                currPlan.setCalories((int) newPlan.get("calories"));
            }
            if (newPlan.containsKey("totalFat")){
                currPlan.setTotalFat((int) newPlan.get("totalFat"));
            }
            if (newPlan.containsKey("sodium")){
                currPlan.setSodium((int) newPlan.get("sodium"));
            }
            if (newPlan.containsKey("carbohydrate")){
                currPlan.setCarbohydrate((int) newPlan.get("carbohydrate"));
            }
            if (newPlan.containsKey("protein")){
                currPlan.setProtein((int) newPlan.get("protein"));
            }
            planRepo.save(currPlan);
        }
        return currPlan;
    }
}
