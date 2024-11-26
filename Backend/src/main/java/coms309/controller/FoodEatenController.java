package coms309.controller;

import coms309.entity.FoodEaten;
import coms309.repository.FoodEatenRepository;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Food Eaten", description = "Food Consumed APIs")
@RestController
public class FoodEatenController {
    @Autowired
    FoodEatenRepository eatenRepo;

    @PostMapping("/consume")
    public FoodEaten createFoodEaten(FoodEaten eaten){
        return eatenRepo.save(eaten);
    }

    @DeleteMapping("/eaten/{id}")
    public FoodEaten delete(@PathVariable int id){
        FoodEaten eaten = eatenRepo.findById(id).orElse(null);
        if (eatenRepo.existsById(id)){
            eatenRepo.deleteById(id);
        }
        return eaten;
    }

}
