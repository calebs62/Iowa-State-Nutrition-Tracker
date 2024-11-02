package coms309.controller;


import coms309.repository.*;
import coms309.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class NotificationsController {
    @Autowired
    NotificationRepository notiRepo;
    @Autowired
    NotificationSettingsRepository notiSettingRepo;
    @Autowired
    UserRepository userRepo;

    @GetMapping("/notifications/settings/{id}")
    public NotificationSettings getPreferences(@PathVariable int userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {return null;}
        NotificationSettings settings = findSetting(userId);
        return settings;
    }
    @GetMapping("/notifications/settings/{id}/{type}")
    public boolean savePreferences(@PathVariable int userId, @PathVariable String type) {
        return checkSetting(userId, type);
    }

    @PutMapping("/notifications/settings/{id}")
    public NotificationSettings setNotificationSettings(@PathVariable int userId, @RequestBody Map<String, Boolean> newSettings) {
        NotificationSettings settings = findSetting(userId);
        if (newSettings.containsKey("system")) {
            settings.setSystem(newSettings.get("system"));
        }
        if (newSettings.containsKey("push")) {
            settings.setPush(newSettings.get("push"));
        }
        if (newSettings.containsKey("reminder")) {
            settings.setReminder(newSettings.get("reminder"));
        }
        if (newSettings.containsKey("sms")) {
            settings.setSMS(newSettings.get("sms"));
        }
        if (newSettings.containsKey("email")) {
            settings.setEmail(newSettings.get("email"));
        }
        return settings;
    }


    /*
    Checks if user has setting
     */
    private boolean checkSetting(int userId, String type) {
        NotificationSettings settings = findSetting(userId);
        if (type.equals("system")) {
            return settings.getSystem();
        }
        else if (type.equals("push")) {
            return settings.getPush();
        }
        else if (type.equals("reminder")) {
            return settings.getReminder();
        }
        else if (type.equals("sms")) {
            return settings.getSMS();
        }
        else if (type.equals("email")) {
            return settings.getEmail();
        }
        else {
            return false;
        }
    }

    private NotificationSettings findSetting(int userId) {
        NotificationSettings settings = notiSettingRepo.findById(userId).orElse(null);
        if(settings == null) {
            settings = new NotificationSettings(userId);
            notiSettingRepo.save(settings);
        }
        return settings;
    }
}
