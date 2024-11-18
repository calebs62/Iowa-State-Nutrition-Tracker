package coms309.controller;

import com.fasterxml.jackson.annotation.JsonView;
import coms309.entity.Achievement;
import coms309.entity.Earned;
import coms309.entity.User;
import coms309.entity.Views;
import coms309.repository.AchievementRepository;
import coms309.repository.EarnedRepository;
import coms309.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Tag(name = "Achievements", description = "Achievement API")
@RestController
public class AchievementController {
    @Autowired
    EarnedRepository earnedRepo;
    @Autowired
    UserRepository userRepo;
    @Autowired
    AchievementRepository achievementRepo;

    @Operation(
            summary = "Create achievement",
            description = "Create achievement object from request body."
    )
    @PostMapping("/achievement")
    @JsonView(value = {Views.Achievement.class})
    public Achievement createAchievement(@Parameter(description = "Achievement object")@RequestBody Achievement achievement){
        return achievementRepo.save(achievement);
    }

    @Operation(
            summary = "Get Achievement",
            description = "Returns an achievement by given id."
    )
    @GetMapping("/achievement/{id}")
    @JsonView(value = {Views.Achievement.class})
    public Achievement getAchievementById(@Parameter(description = "Achievement id")@PathVariable int id){
        return achievementRepo.findById(id).orElse(null);
    }

    @Operation(
            summary = "Get all Achievements",
            description = "Returns all achievements."
    )
    @GetMapping("/allAchievements")
    @JsonView(value = {Views.Achievement.class})
    public List<Achievement> getAllAchievements() {
        return achievementRepo.findAll();
    }

    @Operation(
            summary = "Delete Achievement",
            description = "Deletes achievement by id returns deleted achievement."
    )
    @DeleteMapping("/achievement/{id}")
    @JsonView(value = {Views.Achievement.class})
    public Achievement delete(@Parameter(description = "Achievement id")@PathVariable int id){
        Achievement achievement = achievementRepo.findById(id).orElse(null);
        if (achievementRepo.existsById(id)){
            achievementRepo.deleteById(id);
        }
        return achievement;
    }

    @Operation(
            summary = "Update Achievement",
            description = "Updates achievement based on id and map."
    )
    @PutMapping("/achievement/update/{id}")
    @JsonView(value = {Views.Achievement.class})
    public Achievement updateAchievement(@Parameter(description = "Achievement id")@PathVariable int id, @Parameter(description = "Map containing key value pairs corresponding to fields to be changed.")@RequestBody Map<String, String> map){
        Achievement achievement = achievementRepo.findById(id).orElse(null);
        if (achievement != null) {
            if (map.containsKey("name")){
                achievement.setName(map.get("name"));
            }
            if (map.containsKey("description")){
                achievement.setDescription(map.get("description"));
            }
            if (map.containsKey("icon")){
                achievement.setIcon(map.get("icon"));
            }
            achievementRepo.save(achievement);
        }
        return achievement;
    }

    @Operation(
            summary = "Grants User an Achievement",
            description = "Grants a User denoted by uid an Achievement denoted by aid."
    )
    @PutMapping("/{uid}/earn/{aid}")
    @JsonView(value = {Views.Achievement.class})
    public Achievement userEarnsAch(@Parameter(description = "User id")@PathVariable(value = "uid") int uid, @Parameter(description = "Achievement id")@PathVariable(value = "aid") int aid){
        Achievement achievement = achievementRepo.findById(aid).orElse(null);
        if (achievement != null && achievement.findUser(uid) == null) {
            User user = userRepo.findById(uid).orElse(null);
            if (user ==  null){
                return achievement;
            }
            Earned earned = new Earned(user, achievement);
            earnedRepo.save(earned);
        }
        return achievement;
    }

    @Operation(
            summary = "Revokes Achievement from User",
            description = "Revokes an Achievement denoted by aid from a User denoted by uid."
    )
    @PutMapping("/{uid}/revoke/{aid}")
    @JsonView(value = {Views.Achievement.class})
    public Achievement revokeUserAch(@Parameter(description = "User id")@PathVariable(value = "uid") int uid, @Parameter(description = "Achievement id")@PathVariable(value = "aid") int aid){
        Achievement achievement = achievementRepo.findById(aid).orElse(null);
        if (achievement != null) {
            Earned earned = achievement.findUser(uid);
            if (earned == null) {
                return achievement;
            }
            earnedRepo.delete(earned);
        }
        return achievement;
    }


}
