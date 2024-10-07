package coms309.controller;

import coms309.repository.FoodItemRepository;
import coms309.entity.FoodItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
public class FoodItemController {
    @Autowired
    FoodItemRepository foodRepo;
    // Create item
    @PostMapping("/item")
    public FoodItem saveFoodItem(@RequestBody FoodItem foodItem){
        return foodRepo.save(foodItem);
    }


    // Get item from id
    @GetMapping("/item/{id}")
    public FoodItem getFoodItemById(@PathVariable int id) {
        return foodRepo.findById(id).get();
    }

    // Update item fields
    @PutMapping("/item/update/{id}")
    public FoodItem updateFoodItem(@PathVariable int id, @RequestBody Map<String, Object> foodItem) {
        FoodItem item = foodRepo.findById(id).orElse(null);
        if (item != null) {
            if (foodItem.containsKey("name")) {
                item.setName((String)foodItem.get("name"));
            }
            if (foodItem.containsKey("calories")) {
                item.setCalories((Integer)foodItem.get("calories"));
            }
            if (foodItem.containsKey("totalFat")) {
                item.setTotalFat((Integer)foodItem.get("totalFat"));
            }
            if (foodItem.containsKey("sodium")) {
                item.setSodium((Integer)foodItem.get("sodium"));
            }
            if (foodItem.containsKey("carbohydrate")) {
                item.setCarbohydrate((Integer)foodItem.get("carbohydrate"));
            }
            if (foodItem.containsKey("protein")) {
                item.setProtein((Integer)foodItem.get("protein"));
            }
            if (foodItem.containsKey("servingsize")) {
                item.setServingSize((String)foodItem.get("servingsize"));
            }
            if (foodItem.containsKey("description")) {
                item.setDescription((String)foodItem.get("description"));
            }
            foodRepo.save(item);
        }

        return item;
    }
       // Delete item
    @DeleteMapping("/item/{id}")
    public FoodItem delete(@PathVariable int id) {
        FoodItem val = foodRepo.findById(id).orElse(null);
        if (foodRepo.existsById(id)) {

            foodRepo.deleteById(id);

        }
        return val;
    }

    // List all items
    @GetMapping("/item")
    public List<FoodItem> getAllFoodItems() {
        return new ArrayList<>(foodRepo.findAll());
    }
}
