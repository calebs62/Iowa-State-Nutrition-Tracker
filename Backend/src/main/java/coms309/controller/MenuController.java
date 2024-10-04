package coms309.controller;

import coms309.repository.MenuRepository;
import coms309.entity.Menu;
import coms309.repository.MenuItemRepository;
import coms309.entity.MenuItem;
import coms309.repository.FoodItemRepository;
import coms309.entity.FoodItem;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MenuController {
    @Autowired
    MenuRepository menuRepo;
//    MenuItemRepository menuItemRepo;
//    FoodItemRepository foodItemRepo;

    // Create
    @PostMapping("/menu")
    public Menu saveMenu(@RequestBody Menu menu){
        return menuRepo.save(menu);
    }

    // Read
    @GetMapping("/menu/{id}")
    public Menu getMenubyId(@PathVariable int id){
        return menuRepo.findById(id).get();
    }

    // Update
    @PutMapping("/menu/update/{id}")
    public Menu updateMenu(@PathVariable int id, @RequestBody Menu updatedMenu){
        Menu currMenu = menuRepo.findById(id).orElse(null);
        if (currMenu != null){
            currMenu.setAll(updatedMenu);
            menuRepo.save(currMenu);
        }
        return currMenu;
    }

    // Delete
    @DeleteMapping("/menu/{id}")
    public Menu delete(@PathVariable int id){
        Menu delMenu = menuRepo.findById(id).orElse(null);
        if (menuRepo.existsById(id)){
            menuRepo.deleteById(id);
        }
        return delMenu;
    }

    //List
    @GetMapping("/allMenus")
    public List<Menu> getAllMenus(){
        return new ArrayList<>(menuRepo.findAll());
    }

}
