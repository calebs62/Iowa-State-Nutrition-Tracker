package coms309.FoodItems;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


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

    // Update item

    // Delete item

    // List all items
}
