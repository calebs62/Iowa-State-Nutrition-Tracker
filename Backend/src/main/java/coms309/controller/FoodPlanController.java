package coms309.controller;

import coms309.entity.FoodPlan;
import coms309.repository.FoodPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    @PutMapping("/plan/update/{id}")
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

    // Delete
    @DeleteMapping("/plan/{id}")
    public FoodPlan delete(@PathVariable int id){
        FoodPlan plan = planRepo.findById(id).orElse(null);
        if (planRepo.existsById(id)){
            planRepo.deleteById(id);
        }
        return plan;
    }

    //List all plans
    @GetMapping("/allPlans")
    public List<FoodPlan> getAllPlans(@RequestParam(defaultValue = "") String keyword){
        List<FoodPlan> plans = planRepo.findAll();
        if (keyword.equals("")){
            return plans;
        }
        for (int i = 0; i < plans.size(); i++) {
            FoodPlan plan = plans.get(i);
            String name = plan.getName().toLowerCase();
            String key = keyword.toLowerCase();
            if (!(name.contains(key))){
                plans.remove(plan);
                i--;
            }
        }
        return plans;
    }
}
