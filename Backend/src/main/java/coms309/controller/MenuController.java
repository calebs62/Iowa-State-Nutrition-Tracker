package coms309.controller;

import coms309.repository.MenuRepository;
import coms309.entity.Menu;
import coms309.repository.MenuItemRepository;
import coms309.entity.MenuItem;
import coms309.repository.FoodItemRepository;
import coms309.entity.FoodItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MenuController {
    @Autowired
    MenuRepository menuRepo;
    MenuItemRepository menuItemRepo;
    FoodItemRepository foodItemRepo;

}
