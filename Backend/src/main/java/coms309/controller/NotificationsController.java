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

    @PostMapping("/notifications/settings/{id}")
    public NotificationSettings savePreferences(@PathVariable int userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {return null;}
        NotificationSettings settings = findSetting(userId);
        return settings;
    }

    @PutMapping("/notifications/settings/{id}")
    public NotificationSettings setNotificationSettings(@PathVariable int userId, @RequestBody Map<String, Boolean> newSettings) {
        NotificationSettings settings = findSetting(userId);
        if (newSettings.containsKey("time")) {
            settings.setTimeNotification(newSettings.get("time"));
        }
        if (newSettings.containsKey("system")) {
            settings.setTimeNotification(newSettings.get("system"));
        }
        if (newSettings.containsKey("achievement")) {
            settings.setTimeNotification(newSettings.get("system"));
        }
        return settings;
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
