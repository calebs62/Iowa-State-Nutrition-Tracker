package coms309.controller;

import coms309.repository.FoodItemRepository;
import coms309.entity.FoodItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


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
    @PostMapping("/item/update/{id}")
    public FoodItem updateFoodItem(@PathVariable int id, @RequestBody FoodItem foodItem) {
        FoodItem item = foodRepo.findById(id).orElse(null);
        if (item != null) {
            item.setAll(foodItem);
            foodRepo.save(item);
        }

        return item;
    }
    @PutMapping("/item/update/name/{id}")
    public FoodItem updateName(@PathVariable int id, @RequestBody String val) {
        FoodItem item = foodRepo.findById(id).orElse(null);
        if (item != null) {
            item.setName(val);
            foodRepo.save(item);
        }
        return item;
    }
    @PutMapping("/item/update/calories/{id}")
    public FoodItem updateName(@PathVariable int id, @RequestBody int val) {
        FoodItem item = foodRepo.findById(id).orElse(null);
        if (item != null) {
            item.setCalories(val);
            foodRepo.save(item);
        }
        return item;
    }
    @PutMapping("/item/update/totalfat/{id}")
    public FoodItem updateTotalFat(@PathVariable int id, @RequestBody int val) {
        FoodItem item = foodRepo.findById(id).orElse(null);
        if (item != null) {
            item.setTotalFat(val);
            foodRepo.save(item);
        }
        return item;
    }

    @PutMapping("/item/update/sodium/{id}")
    public FoodItem updateSodium(@PathVariable int id, @RequestBody int val) {
        FoodItem item = foodRepo.findById(id).orElse(null);
        if (item != null) {
            item.setSodium(val);
            foodRepo.save(item);
        }
        return item;
    }
    @PutMapping("/item/update/carbohydrate/{id}")
    public FoodItem updateCarbohydrate(@PathVariable int id, @RequestBody int val) {
        FoodItem item = foodRepo.findById(id).orElse(null);
        if (item != null) {
            item.setCarbohydrate(val);
            foodRepo.save(item);
        }
        return item;
    }
    @PutMapping("/item/update/protein/{id}")
    public FoodItem updateProtein(@PathVariable int id, @RequestBody int val) {
        FoodItem item = foodRepo.findById(id).orElse(null);
        if (item != null) {
            item.setProtein(val);
            foodRepo.save(item);
        }
        return item;
    }

    @PutMapping("/item/update/servingsize/{id}")
    public FoodItem updateServingSize(@PathVariable int id, @RequestBody String val) {
        FoodItem item = foodRepo.findById(id).orElse(null);
        if (item != null) {
            item.setServingSize(val);
            foodRepo.save(item);
        }
        return item;
    }
    @PutMapping("/item/update/description/{id}")
    public FoodItem updateDescription(@PathVariable int id, @RequestBody String val) {
        FoodItem item = foodRepo.findById(id).orElse(null);
        if (item != null) {
            item.setDescription(val);
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
