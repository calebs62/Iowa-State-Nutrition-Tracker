package coms309.controller;

import coms309.entity.FoodEaten;
import coms309.repository.FoodEatenRepository;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Tag(name = "Food Eaten", description = "Food Consumed APIs")
@RestController
public class FoodEatenController {
    @Autowired
    FoodEatenRepository eatenRepo;

    @PostMapping("/eaten")
    public FoodEaten createFoodEaten(FoodEaten eaten){
        return eatenRepo.save(eaten);
    }

    @GetMapping("/eaten/{id}")
    public FoodEaten getEatenById(@PathVariable int id){
        return eatenRepo.findById(id).orElse(null);
    }

    @GetMapping("/allEaten")
    public List<FoodEaten> getAllEaten(){
        return eatenRepo.findAll();
    }

    @GetMapping("/eaten/user/{uid}")
    public List<FoodEaten> getEatenByUser(@PathVariable int uid){
        List<FoodEaten> eatenList = eatenRepo.findAll();
        List<FoodEaten> userEaten = new ArrayList<>();
        for (FoodEaten e: eatenList){
            if (e.getUser().getUid() == uid){
                userEaten.add(e);
            }
        }
        return userEaten;
    }

    @GetMapping("/eaten/user/{uid}/{startTime}/{endTime}")
    public List<FoodEaten> getEatenByUserTime(@PathVariable(name = "uid") int uid, @PathVariable(name = "startTime") Date startTime, @PathVariable(name = "endTime") Date endTime){
        List<FoodEaten> userEaten = new ArrayList<>();
        List<FoodEaten> timeEaten = eatenRepo.findAllByDateBetween(startTime, endTime);
        for (FoodEaten e: timeEaten){
            if (e.getUser().getUid() == uid){
                userEaten.add(e);
            }
        }
        return userEaten;
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
