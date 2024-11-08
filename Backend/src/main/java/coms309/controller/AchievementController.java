package coms309.controller;

import com.fasterxml.jackson.annotation.JsonView;
import coms309.entity.Achievement;
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


}
