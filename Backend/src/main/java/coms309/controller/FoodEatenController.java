package coms309.controller;

import com.fasterxml.jackson.annotation.JsonView;
import coms309.entity.FoodEaten;
import coms309.entity.FoodItem;
import coms309.entity.User;
import coms309.entity.Views;
import coms309.repository.FoodEatenRepository;
import coms309.repository.FoodItemRepository;
import coms309.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(name = "Food Eaten", description = "Food Consumed APIs")
@RestController
public class FoodEatenController {
    @Autowired
    UserRepository userRepo;
    @Autowired
    FoodItemRepository itemRepo;

    private static FoodEatenRepository eatenRepo;
    @Autowired
    public void setRepo(FoodEatenRepository eRepo){
        eatenRepo = eRepo;
    }

    @PostMapping("/eaten")
    @JsonView(value = {Views.FoodEaten.class})
    public FoodEaten createFoodEaten(@RequestBody Map<String, Object> map){
        Integer userId = (Integer) map.get("userId");
        Integer foodId = (Integer) map.get("foodId");
        Integer servings = (Integer) map.get("servings");

        User user = userRepo.findById(userId).orElse(null);
        FoodItem item = itemRepo.findById(foodId).orElse(null);
        if (user == null || item == null || servings == null) {
            return null;
        }

        FoodEaten eaten = new FoodEaten(user, item, servings);

        return eatenRepo.save(eaten);
    }

    @GetMapping("/eaten/{id}")
    @JsonView(value = {Views.FoodEaten.class})
    public FoodEaten getEatenById(@PathVariable int id){
        return eatenRepo.findById(id).orElse(null);
    }

    @GetMapping("/allEaten")
    @JsonView(value = {Views.FoodEaten.class})
    public List<FoodEaten> getAllEaten(){
        return eatenRepo.findAll();
    }

    @GetMapping("/eaten/user/{uid}")
    @JsonView(value = {Views.FoodEaten.class})
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

    @GetMapping("/eaten/user/{uid}/time")
    @JsonView(value = {Views.FoodEaten.class})
    public static List<FoodEaten> getEatenByUserTime(@PathVariable(name = "uid") int uid, @RequestParam(required = false) String start, @RequestParam(required = false) String end) {
        LocalDateTime startTime = start != null ? LocalDateTime.parse(start): LocalDateTime.now().minusMonths(1);   //Default to last month
        LocalDateTime endTime = start != null ? LocalDateTime.parse(end): LocalDateTime.now(); //Default to now

        List<FoodEaten> userEaten = new ArrayList<>();
        List<FoodEaten> timeEaten = eatenRepo.findAllByDateBetween(startTime, endTime);
        for (FoodEaten e : timeEaten) {
            if (e.getUser().getUid() == uid) {
                userEaten.add(e);
            }
        }
        return userEaten;
    }

    @DeleteMapping("/eaten/{id}")
    @JsonView(value = {Views.FoodEaten.class})
    public Boolean delete(@PathVariable int id){
        FoodEaten eaten = eatenRepo.findById(id).orElse(null);
        if (eatenRepo.existsById(id)){
            eatenRepo.deleteById(id);
        }
        return !eatenRepo.existsById(id);   //Returns Success/Failure of deletion
    }

    @PutMapping("/eaten/update/{id}")
    @JsonView(value = {Views.FoodEaten.class})
    public FoodEaten update(@PathVariable int id, @RequestBody Map<String, Object> map){
        FoodEaten eaten = eatenRepo.findById(id).orElse(null);
        if (eaten != null) {
            if (map.containsKey("date")){
                eaten.setDate((LocalDateTime) map.get("date"));
            }
            if (map.containsKey("servings")){
                eaten.setServings((Integer) map.get("servings"));
            }
            if (map.containsKey("food")){
                eaten.setFood((FoodItem) map.get("food"));
            }
            if (map.containsKey("user")){
                eaten.setUser((User) map.get("user"));
            }
        }
        return eaten;
    }

}
