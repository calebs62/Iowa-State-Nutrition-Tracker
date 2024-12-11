package coms309.controller;

import com.fasterxml.jackson.annotation.JsonView;
import coms309.entity.*;
import coms309.repository.AchievementRepository;
import coms309.repository.EarnedRepository;
import coms309.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Tag(name = "Achievements", description = "Achievement API")
@RestController
public class AchievementController {
    @Autowired
    UserRepository userRepo;

    private static EarnedRepository earnedRepo;
    private static AchievementRepository achievementRepo;

    @Autowired
    public void setStaticRepo(AchievementRepository aRepo, EarnedRepository eRepo){
        achievementRepo = aRepo;
        earnedRepo = eRepo;
    }

    @Operation(
            summary = "Create achievement",
            description = "Create achievement object from request body."
    )
    @PostMapping("/achievement")
    @JsonView(value = {Views.Achievement.class})
    public Achievement createAchievement(@Parameter(description = "Achievement object")@RequestBody Map<String, Object> map){
        String name = (String) map.get("name");
        String description = (String) map.get("description");
        String icon = (String) map.get("icon");
        Integer goal = (Integer) map.get("goal");

        Achievement achievement = new Achievement(name, description, icon, goal);
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
    public Achievement updateAchievement(@Parameter(description = "Achievement id")@PathVariable int id, @Parameter(description = "Map containing key value pairs corresponding to fields to be changed.")@RequestBody Map<String, Object> map){
        Achievement achievement = achievementRepo.findById(id).orElse(null);
        if (achievement != null) {
            if (map.containsKey("name")){
                achievement.setName((String) map.get("name"));
            }
            if (map.containsKey("description")){
                achievement.setDescription((String) map.get("description"));
            }
            if (map.containsKey("icon")){
                achievement.setIcon((String) map.get("icon"));
            }
            if (map.containsKey("goal")){
                achievement.setGoal((Integer) map.get("goal"));
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

    @PutMapping("/check/achievements/{uid}")
    @JsonView(value = {Views.Achievement.class})
    public Set<Earned> checkUserAchs(@PathVariable int uid){
        User user = userRepo.findById(uid).orElse(null);
        if (user == null) {
            return null;
        }
        consecutiveProtein(user);
        socialButterfly(user);
        return user.getEarned();
    }

    //Auto on login
    public static Earned consecutiveLogins(User user, Timestamp lastLogin){
        Timestamp today = new Timestamp(System.currentTimeMillis());
        Timestamp safeTime = new Timestamp(lastLogin.getTime() + (1000 * 60 * 60 * 24));

        if (today.before(safeTime)){
            Achievement achievement = achievementRepo.findById(2).orElse(null);
            if (achievement == null) {
                return null;
            }
            EarnedKey id = new EarnedKey(user.getUid(), 2);
            Earned earned = earnedRepo.findById(id).orElse(null);
            if (earned == null) {
                Earned newEarned = new Earned(user, achievement);
                newEarned.setProgress(1);
                return earnedRepo.save(newEarned);
            }
            earned.addProgress();
            earned.setEarnDate(new Date()); //Sets time to now
            if (earned.getProgress() >= achievement.getGoal()){
                earned.setHasEarned(true);
            }
            return earnedRepo.save(earned);
        }
        return null;
    }

    // Manually check with endpoint
    public Earned consecutiveProtein(User user){
        Achievement achievement = achievementRepo.findById(3).orElse(null);
        if (achievement == null || user == null) {
            return null;
        }
        EarnedKey eid = new EarnedKey(user.getUid(), 3);
        Earned earn = earnedRepo.findById(eid).orElse(null);

        if (earn == null) {
            earn = new Earned(user, achievement);
        }

        List<FoodEaten> userEatenList = FoodEatenController.getEatenByUserTime(user.getUid(), LocalDateTime.now().minusWeeks(1).toString(), LocalDateTime.now().toString());
        int totalProtein = 0;
        for (FoodEaten eaten : userEatenList) {
            totalProtein += eaten.getFood().getProtein();
        }

        Set<GroupMember> membered = user.getMembered();
        int planProtein = 0;
        for (GroupMember mem: membered){
            Group group = mem.getGroup();
            FoodPlan plan = group.getPlan();
            planProtein = plan.getProtein();
        }

        if (planProtein <= totalProtein && totalProtein > 0){
            earn.addProgress();
        }

        if (!earn.getHasEarned() && earn.getProgress() >= achievement.getGoal()){
            earn.setHasEarned(true);
        }

        return earnedRepo.save(earn);
    }

    public Earned socialButterfly(User user){
        Achievement achievement = achievementRepo.findById(4).orElse(null);
        if (achievement == null || user == null) {
            return null;
        }
        EarnedKey eid = new EarnedKey(user.getUid(), achievement.getId());
        Earned earn = earnedRepo.findById(eid).orElse(null);
        if (earn == null) {
            earn = new Earned(user, achievement);
        }

        Set<GroupMember> memberSet = user.getMembered();
        for (GroupMember member : memberSet){
            int numMess = member.getMessages().size();
            earn.setProgress(numMess);
            if(!earn.getHasEarned() && numMess >= achievement.getGoal()){
                earn.setHasEarned(true);
            }
        }
        return earnedRepo.save(earn);
    }


}
