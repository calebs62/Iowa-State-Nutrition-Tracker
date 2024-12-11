package coms309.controller;

import com.fasterxml.jackson.annotation.JsonView;
import coms309.entity.Views;
import coms309.repository.MenuRepository;
import coms309.entity.Menu;
import coms309.repository.FoodItemRepository;
import coms309.entity.FoodItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Menus", description = "Menu API")
@RestController
public class MenuController {
    @Autowired
    MenuRepository menuRepo;
    @Autowired
    FoodItemRepository foodRepo;

    // Create
    @Operation(
            summary = "Create Menu",
            description = "Create menu object from request body."
    )
    @PostMapping("/menu")
    @JsonView(value = {Views.Menu.class})
    public Menu saveMenu(@Parameter(description = "Menu object")@RequestBody Menu menu){
        return menuRepo.save(menu);
    }

    // Read
    @Operation(
            summary = "Get Menu",
            description = "Returns menu by given id."
    )
    @GetMapping("/menu/{id}")
    @JsonView(value = {Views.Menu.class})
    public Menu getMenubyId(@Parameter(description = "Menu id")@PathVariable int id){
        return menuRepo.findById(id).orElse(null);
    }

    // Update
    @Operation(
            summary = "Update Menu",
            description = "Updates menu based on id and menu object."
    )
    @PutMapping("/menu/update/{id}")
    @JsonView(value = {Views.Menu.class})
    public Menu updateMenu(@Parameter(description = "Menu id")@PathVariable int id, @Parameter(description = "Updated menu object")@RequestBody Menu updatedMenu){
        Menu currMenu = menuRepo.findById(id).orElse(null);
        if (currMenu != null){
            currMenu.setAll(updatedMenu);
            menuRepo.save(currMenu);
        }
        return currMenu;
    }

    // Add item to menu
    @Operation(
            summary = "Add Menu Item",
            description = "Add food item to menu."
    )
    @PutMapping("/menu/{menuid}/add/{foodid}")
    @JsonView(value = {Views.Menu.class})
    public Menu addFood(@Parameter(description = "Menu id")@PathVariable("menuid") int menuId, @Parameter(description = "Food item id")@PathVariable("foodid") int foodId){
        Menu currMenu = menuRepo.findById(menuId).orElse(null);
        FoodItem newItem = foodRepo.findById(foodId).orElse(null);
        if (currMenu != null && newItem != null){
            currMenu.addFoodItem(newItem);
            menuRepo.save(currMenu);
        }
        return currMenu;
    }

    // Remove item from menu
    @Operation(
            summary = "Remove Menu Item",
            description = "Removes food item from menu."
    )
    @PutMapping("/menu/{menuid}/remove/{foodid}")
    @JsonView(value = {Views.Menu.class})
    public Menu removeFood(@Parameter(description = "Menu id")@PathVariable("menuid") int menuId, @Parameter(description = "Food item id")@PathVariable("foodid") int foodId){
        Menu currMenu = menuRepo.findById(menuId).orElse(null);
        FoodItem delItem = foodRepo.findById(foodId).orElse(null);
        if (currMenu != null && delItem != null){
            currMenu.removeFoodItem(delItem);
            menuRepo.save(currMenu);
        }
        return currMenu;
    }

    // Delete
    @Operation(
            summary = "Delete Menu",
            description = "Deletes menu by given id."
    )
    @DeleteMapping("/menu/{id}")
    @JsonView(value = {Views.Menu.class})
    public Menu delete(@Parameter(description = "Group id")@PathVariable int id){
        Menu delMenu = menuRepo.findById(id).orElse(null);
        if (menuRepo.existsById(id)){
            menuRepo.deleteById(id);
        }
        return delMenu;
    }

    //List
    @Operation(
            summary = "Get All Menus",
            description = "Returns a list of all menus."
    )
    @GetMapping("/allMenus")
    @JsonView(value = {Views.Menu.class})
    public List<Menu> getAllMenus(){
        return new ArrayList<>(menuRepo.findAll());
    }

    // Search by keyword
    @Operation(
            summary = "Search Menus",
            description = "Returns a list of menus with name containing keyword."
    )
    @GetMapping("/menu/search")
    @JsonView(value = {Views.Menu.class})
    public List<Menu> searchMenus(@Parameter(description = "String keyword to search by")@RequestParam(defaultValue="") String keyword){
        List<Menu> menus = menuRepo.findAll();
        if (keyword.isEmpty()){
            return menus;
        }
        for(Menu m : menus){
            String name = m.getName().toLowerCase();
            String key = keyword.toLowerCase();
            if (!(name.contains(key))){
                menus.remove(m);
            }
        }
        return menus;
    }

}
