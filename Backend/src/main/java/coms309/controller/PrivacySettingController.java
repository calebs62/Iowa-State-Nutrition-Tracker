package coms309.controller;

import coms309.entity.PrivacySettings;
import coms309.entity.SystemNotificationQueue;
import coms309.entity.User;
import coms309.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
public class PrivacySettingController {
    @Autowired
    PrivacySettingRepository privRepo;
    @Autowired
    UserRepository userRepo;

    @GetMapping("/privacy/settings/{id}")
    public PrivacySettings getPreferences(@PathVariable int userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {return null;}
        return findSetting(userId);
    }
    @GetMapping("/ptivacy/settings/{id}/{type}")
    public boolean savePreferences(@PathVariable int userId, @PathVariable String type) {
        return checkSetting(userId, type);
    }

    @PutMapping("/privacy/settings/{id}")
    public PrivacySettings setNotificationSettings(@PathVariable int userId, @RequestBody Map<String, Boolean> newSettings) {
        PrivacySettings settings = findSetting(userId);
        if (newSettings.containsKey("food")) {
            settings.setFood(newSettings.get("food"));
        }
        if (newSettings.containsKey("goal")) {
            settings.setGoal(newSettings.get("goal"));
        }
        if (newSettings.containsKey("achievement")) {
            settings.setAchievement(newSettings.get("achievement"));
        }
        return settings;
    }

    /*
    Checks if user has setting
     */
    private boolean checkSetting(int userId, String type) {
        PrivacySettings settings = findSetting(userId);
        if (type.equals("food")) {
            return settings.getFood();
        }
        else if (type.equals("goal")) {
            return settings.getGoal();
        }
        else if (type.equals("achievement")) {
            return settings.getAchievement();
        }
        else {
            return false;
        }
    }

    private PrivacySettings findSetting(int userId) {
        PrivacySettings settings = privRepo.findById(userId).orElse(null);
        User user = userRepo.findById(userId).orElse(null);
        if (user.equals(null)) {
            return null;
        }

        if(settings == null) {
            settings = new PrivacySettings(userId, user);
            privRepo.save(settings);
        }
        return settings;
    }

}
