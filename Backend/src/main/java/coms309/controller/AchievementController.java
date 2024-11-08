package coms309.controller;

import com.fasterxml.jackson.annotation.JsonView;
import coms309.entity.Achievement;
import coms309.entity.Earned;
import coms309.entity.User;
import coms309.entity.Views;
import coms309.repository.AchievementRepository;
import coms309.repository.EarnedRepository;
import coms309.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class AchievementController {
    @Autowired
    EarnedRepository earnedRepo;
    @Autowired
    UserRepository userRepo;
    @Autowired
    AchievementRepository achievementRepo;


    @PostMapping("/achievement")
    @JsonView(value = {Views.Achievement.class})
    public Achievement createAchievement(@RequestBody Achievement achievement){
        return achievementRepo.save(achievement);
    }

    @GetMapping("/achievement/{id}")
    @JsonView(value = {Views.Achievement.class})
    public Achievement getAchievementById(@PathVariable int id){
        return achievementRepo.findById(id).orElse(null);
    }

    @GetMapping("/allAchievements")
    @JsonView(value = {Views.Achievement.class})
    public List<Achievement> getAllAchievements() {
        return achievementRepo.findAll();
    }

    @DeleteMapping("/achievement/{id}")
    @JsonView(value = {Views.Achievement.class})
    public Achievement delete(@PathVariable int id){
        Achievement achievement = achievementRepo.findById(id).orElse(null);
        if (achievementRepo.existsById(id)){
            achievementRepo.deleteById(id);
        }
        return achievement;
    }

    @PutMapping("/achievement/update/{id}")
    @JsonView(value = {Views.Achievement.class})
    public Achievement updateAchievement(@PathVariable int id, @RequestBody Map<String, String> map){
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

    @PutMapping("/{uid}/earn/{aid}")
    @JsonView(value = {Views.Achievement.class})
    public Achievement userEarnsAch(@PathVariable(value = "uid") int uid, @PathVariable(value = "aid") int aid){
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

    @PutMapping("/{uid}/revoke/{aid}")
    @JsonView(value = {Views.Achievement.class})
    public Achievement revokeUserAch(@PathVariable(value = "uid") int uid, @PathVariable(value = "aid") int aid){
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
