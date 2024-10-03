package coms309.controller;

import coms309.repository.FoodItemRepository;
import coms309.entity.FoodItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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


    // Get all
    @GetMapping("/item")
    public List<FoodItem> getAllFoodItems() {
        List<FoodItem> list = new ArrayList<>();
        list.addAll(foodRepo.findAll());
        return list;
    }

    // Get item from id
    @GetMapping("/item/{id}")
    public FoodItem getFoodItemById(@PathVariable int id) {
        return foodRepo.findById(id).get();
    }

    // Update item

    // Delete item

    // List all items
}
