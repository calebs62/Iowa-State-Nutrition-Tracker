package coms309.controller;


import coms309.repository.*;
import coms309.entity.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Tag(name="Notifications", description="Notifications API")
@RestController
public class NotificationsController {
    @Autowired
    NotificationRepository notiRepo;
    @Autowired
    NotificationSettingsRepository notiSettingRepo;
    @Autowired
    UserRepository userRepo;
    @Autowired
    SystemNotificationQueueRepository sysNotRepo;

    @GetMapping("/notifications/settings/{userId}")
    public ResponseEntity<?> getPreferences(@PathVariable int userId) {
        try {
            User user = userRepo.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            NotificationSettings settings = findSetting(userId);
            return ResponseEntity.ok(settings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error retrieving settings: " + e.getMessage());
        }
    }

    @GetMapping("/notifications/settings/{userId}/{type}")
    public boolean savePreferences(@PathVariable int userId, @PathVariable String type) {
        return checkSetting(userId, type);
    }

    @PutMapping("/notifications/settings/{userId}")
    public ResponseEntity<?> setNotificationSettings(@PathVariable int userId,
                                                     @RequestBody Map<String, Boolean> newSettings) {
        try {
            NotificationSettings settings = findSetting(userId);
            if (settings == null) {
                settings = new NotificationSettings(userId);
            }

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

            settings = notiSettingRepo.save(settings);
            return ResponseEntity.ok(settings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error updating settings: " + e.getMessage());
        }
    }

    @PostMapping("/notifications/system")
    private SystemNotificationQueue queue(@RequestBody Map<String, Object> data) {
        SystemNotificationQueue newNot;
        if (data.containsKey("time")) {
            newNot =  new SystemNotificationQueue((String)data.get("header"), (String)data.get("body"), (Timestamp)data.get("time"));
        }
        else {
            newNot =  new SystemNotificationQueue((String)data.get("header"), (String)data.get("body"));
        }
        sysNotRepo.save(newNot);
        return newNot;
    }

    @DeleteMapping("/notifications/system/{id}")
    private SystemNotificationQueue removeId(@PathVariable int id) {
        SystemNotificationQueue queue = sysNotRepo.findById(id).orElse(null);
        if (sysNotRepo.existsById(id)) {
            sysNotRepo.deleteById(id);
        }
        return queue;
    }
    @DeleteMapping("/notifications/system/before")
    private Set<SystemNotificationQueue> removeBefore(@RequestBody Timestamp time) {
        Set<SystemNotificationQueue> queue = new HashSet<SystemNotificationQueue>();
        queue.addAll(sysNotRepo.findAll());
        Set<SystemNotificationQueue> ret = new HashSet<SystemNotificationQueue>();
        for (SystemNotificationQueue i : queue) {
            if (i.getTime().before(time)) {
                ret.add(i);
                queue.remove(i);
                sysNotRepo.deleteById(i.getId());
            }
        }
        return ret;
    }

    @GetMapping("/notifications/system/user/{userId}")
    public ResponseEntity<?> userNewNotifs(@PathVariable int userId) {
        try {
            User user = userRepo.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            Set<SystemNotificationQueue> queue = new HashSet<>(sysNotRepo.findAll());
            Set<SystemNotificationQueue> ret = new HashSet<>();

            for (SystemNotificationQueue notification : queue) {
                if (notification.getTime() != null &&
                        user.getLastLogin() != null &&
                        notification.getTime().after(user.getLastLogin())) {
                    ret.add(notification);
                }
            }

            return ResponseEntity.ok(ret);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error fetching notifications: " + e.getMessage());
        }
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
        if (settings == null) {
            settings = new NotificationSettings(userId);
            try {
                settings = notiSettingRepo.save(settings);
            } catch (Exception e) {
                e.printStackTrace();
                // Try finding by other fields if save fails
                settings = notiSettingRepo.findByUserUidOrUserOrUserId(userId, userId, userId).orElse(null);
                if (settings == null) {
                    // If still not found, create new with generated ID
                    settings = new NotificationSettings(userId);
                    settings = notiSettingRepo.save(settings);
                }
            }
        }
        return settings;
    }
}
