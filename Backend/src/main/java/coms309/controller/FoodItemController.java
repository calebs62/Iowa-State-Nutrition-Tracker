package coms309.controller;

import coms309.repository.FoodItemRepository;
import coms309.entity.FoodItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Tag(name="Food Item", description="Food nuritional information APIs")
@RestController
public class FoodItemController {
    @Autowired
    FoodItemRepository foodRepo;
    // Create item
    @Operation(
            summary="Create item",
            description="Create  fooditem object from request body."
    )
    @PostMapping("/item")
    public FoodItem saveFoodItem(@RequestBody FoodItem foodItem){
        return foodRepo.save(foodItem);
    }


    // Get item from id
    @Operation(
            summary="Get foodItem",
            description="Get food item based on id"
    )
    @GetMapping("/item/{id}")
    public FoodItem getFoodItemById(@Parameter(description="Id of food item to be returned.")@PathVariable int id) {
        return foodRepo.findById(id).get();
    }

    // Update item fields
    @Operation(
            summary="Update food item",
            description="Update food item based on id"
    )
    @PutMapping("/item/update/{id}")
    public FoodItem updateFoodItem(@Parameter(description="Id of food item to be updated.")@PathVariable int id, @Parameter(description="Map containing certain key value pairs corrosponding to fields to be changed.")@RequestBody Map<String, Object> foodItem) {
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
       @Operation(
               summary="Delete foodItem",
               description="Delete food item based on id"
       )
    @DeleteMapping("/item/{id}")
    public FoodItem delete(@Parameter(description="Id of food item to be deleted.")@PathVariable int id) {
        FoodItem val = foodRepo.findById(id).orElse(null);
        if (foodRepo.existsById(id)) {

            foodRepo.deleteById(id);

        }
        return val;
    }

    @Operation(
            summary="Get list of foodItem",
            description="Get list of food item based on criteria"
    )
    // List all items
    @GetMapping("/item")
    public List<FoodItem> getAllFoodItems(@Parameter(description="Map containing search criteria to filter search results by")@RequestBody(required = false) Map<String, Object> searchTerms) {
        /*
            Map will contain search terms if values have been entered
            Keys : info
            name : String that contains search term, will check if that term is present anywhere in an item's name
            description: String that contains search term, will check if that term is present anywhere in an item's description

            THESE REPEAT FOR EACH COLUMN IF THE TABLE (calories, carbohydrate, protein, sodium, totalfat)
            calories : Integer value as a String
            caloriecomp : one of {<, <=, ==, >=, >} as a String value, < would search for terms less than the value of calories



         */

        List<FoodItem> items = foodRepo.findAll();
        List<FoodItem> itemsToRemove = new ArrayList<FoodItem>();
        if (searchTerms != null) {
            for (FoodItem item : items) {
                if (searchTerms.containsKey("name") && !(item.getName().toLowerCase().contains(((String)searchTerms.get("name")).toLowerCase()))) {
                    itemsToRemove.add(item);
                }
                if (searchTerms.containsKey("description") && !(item.getDescription().toLowerCase().contains(((String)searchTerms.get("description")).toLowerCase()))) {
                    itemsToRemove.add(item);
                }
                if (searchTerms.containsKey("calories") && !(compareVals(item.getCalories(), (String)searchTerms.get("calories"), (String)searchTerms.get("caloriescomp")))) {
                    itemsToRemove.add(item);
                }
                if (searchTerms.containsKey("carbohydrate") && !(compareVals(item.getCarbohydrate(), (String)searchTerms.get("carbohydrate"), (String)searchTerms.get("carbohydratecomp")))) {
                    itemsToRemove.add(item);
                }
                if (searchTerms.containsKey("protein") && !(compareVals(item.getProtein(), (String)searchTerms.get("protein"), (String)searchTerms.get("proteincomp")))) {
                    itemsToRemove.add(item);
                }
                if (searchTerms.containsKey("sodium") && !(compareVals(item.getSodium(), (String)searchTerms.get("sodium"), (String)searchTerms.get("sodiumcomp")))) {
                    itemsToRemove.add(item);
                }
                if (searchTerms.containsKey("totalfat") && !(compareVals(item.getTotalFat(), (String)searchTerms.get("totalfat"), (String)searchTerms.get("totalfatcomp")))) {
                    itemsToRemove.add(item);
                }
            }
            items.removeAll(itemsToRemove);

        }
        return items;
    }

    private boolean compareVals(int foodNumber, String on, String comparison) {
        int otherNumber = Integer.parseInt(on);
        if ((Objects.equals(comparison, "<") || Objects.equals(comparison, "<=")) && foodNumber < otherNumber) {
            return true;
        }
        else if ((Objects.equals(comparison, ">") || Objects.equals(comparison, ">=")) && foodNumber > otherNumber) {
            return true;
        }
        else if ((Objects.equals(comparison, "<=") || Objects.equals(comparison, ">=") || Objects.equals(comparison, "==")) && foodNumber == otherNumber) {
            return true;
        }
        return false;
    }

}
