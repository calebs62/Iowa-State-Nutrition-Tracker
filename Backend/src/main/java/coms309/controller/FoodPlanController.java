package coms309.controller;

import com.fasterxml.jackson.annotation.JsonView;
import coms309.entity.FoodPlan;
import coms309.entity.Views;
import coms309.repository.FoodPlanRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "FoodPlan", description = "FoodPlan API")
@RestController
public class FoodPlanController {
    @Autowired
    FoodPlanRepository planRepo;

    // Create
    @Operation(
            summary="Create FoodPlan",
            description="Create foodPlan object from request body."
    )
    @PostMapping("/plan")
    @JsonView(Views.FoodPlan.class)
    public FoodPlan savePlan(@Parameter(description = "FoodPlan object")@RequestBody FoodPlan plan){
        return planRepo.save(plan);
    }

    // Read
    @Operation(
            summary="Get FoodPlan",
            description="Returns foodPlan by given id."
    )
    @GetMapping("/plan/{id}")
    @JsonView(Views.FoodPlan.class)
    public FoodPlan getPlanById(@Parameter(description = "FoodPlan id")@PathVariable int id){
        return planRepo.findById(id).orElse(null);
    }

    // Update
    @Operation(
            summary="Update FoodPlan",
            description="Updates foodPlan based on id and map."
    )
    @PutMapping("/plan/update/{id}")
    @JsonView(Views.FoodPlan.class)
    public FoodPlan updatePlan(@Parameter(description = "FoodPlan id")@PathVariable int id, @Parameter(description = "Map containing certain key value pairs corresponding to fields to be changed.")@RequestBody Map<String, Object> map){
        FoodPlan currPlan = planRepo.findById(id).orElse(null);
        if (currPlan != null){
            if (map.containsKey("name")){
                currPlan.setName((String) map.get("name"));
            }
            if (map.containsKey("calories")){
                currPlan.setCalories((int) map.get("calories"));
            }
            if (map.containsKey("totalFat")){
                currPlan.setTotalFat((int) map.get("totalFat"));
            }
            if (map.containsKey("sodium")){
                currPlan.setSodium((int) map.get("sodium"));
            }
            if (map.containsKey("carbohydrate")){
                currPlan.setCarbohydrate((int) map.get("carbohydrate"));
            }
            if (map.containsKey("protein")){
                currPlan.setProtein((int) map.get("protein"));
            }
            planRepo.save(currPlan);
        }
        return currPlan;
    }

    // Delete
    @Operation(
            summary="Delete FoodPlan",
            description="Deletes foodPlan by id returns deleted foodPlan."
    )
    @DeleteMapping("/plan/{id}")
    @JsonView(Views.FoodPlan.class)
    public FoodPlan delete(@Parameter(description = "FoodPlan id")@PathVariable int id){
        FoodPlan plan = planRepo.findById(id).orElse(null);
        if (planRepo.existsById(id)){
            planRepo.deleteById(id);
        }
        return plan;
    }

    //List all plans
    @Operation(
            summary="Get & Search All FoodPlans",
            description="Returns a list of groups with name containing keyword if keyword not empty."
    )
    @GetMapping("/allPlans")
    @JsonView(Views.FoodPlan.class)
    public List<FoodPlan> getAllPlans(@Parameter(description = "String keyword to search by")@RequestParam(defaultValue = "") String keyword){
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
