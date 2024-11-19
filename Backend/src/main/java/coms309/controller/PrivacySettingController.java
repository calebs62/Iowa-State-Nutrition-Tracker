package coms309.controller;

import coms309.entity.PrivacySettings;
import coms309.entity.SystemNotificationQueue;
import coms309.entity.User;
import coms309.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Tag(name="Privacy Settings", description="Privacy Settings API")
@RestController
@RequestMapping("/privacy/settings")
public class PrivacySettingController {
    private static final Logger logger = LoggerFactory.getLogger(PrivacySettingController.class);

    @Autowired
    PrivacySettingRepository privRepo;

    @Autowired
    UserRepository userRepo;

    @Operation(
            summary="Get privacy settings for user",
            description="Returns given user's privacy settings"
    )
    @GetMapping("/{userId}")
    public ResponseEntity<?> getPreferences(@Parameter(description="Id of user who's notification settings are to be retrieved.")@PathVariable int userId) {
        logger.info("Getting privacy settings for user: " + userId);
        PrivacySettings settings = findSetting(userId);
        if (settings == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(settings);
    }

    @Operation(
            summary="Updated privacy settings",
            description="Updated privacy settings for given user."
    )
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateSettings(@Parameter(description="Id of user who's privacy settings are to be updated.")@PathVariable int userId,
                                            @Parameter(description="Map containing settings and whether they should be changed.")@RequestBody Map<String, Boolean> newSettings) {
        logger.info("Updating privacy settings for user: " + userId + " with: " + newSettings);

        PrivacySettings settings = findSetting(userId);
        if (settings == null) {
            return ResponseEntity.notFound().build();
        }

        if (newSettings.containsKey("food")) {
            settings.setFood(newSettings.get("food"));
        }
        if (newSettings.containsKey("goal")) {
            settings.setGoal(newSettings.get("goal"));
        }
        if (newSettings.containsKey("achievement")) {
            settings.setAchievement(newSettings.get("achievement"));
        }

        try {
            settings = privRepo.save(settings);
            logger.info("Successfully updated settings: " + settings);
            return ResponseEntity.ok(settings);
        } catch (Exception e) {
            logger.error("Error updating settings", e);
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private PrivacySettings findSetting(int userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            logger.error("User not found: " + userId);
            return null;
        }

        PrivacySettings settings = privRepo.findById(userId).orElse(null);
        if (settings == null) {
            logger.info("Creating new privacy settings for user: " + userId);
            settings = new PrivacySettings(userId);
            try {
                settings = privRepo.save(settings);
                logger.info("Created new privacy settings: " + settings);
            } catch (Exception e) {
                logger.error("Error creating privacy settings", e);
                e.printStackTrace();
                return null;
            }
        }

        return settings;
    }
}