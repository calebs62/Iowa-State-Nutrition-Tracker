package coms309.controller;

import coms309.entity.ActivityFeed;
import coms309.entity.Group;
import coms309.entity.ImageGallery;
import coms309.entity.User;
import coms309.repository.ActivityFeedRepository;
import coms309.repository.GroupRepository;
import coms309.repository.ImageGalleryRepository;
import coms309.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@Tag(name = "Activity Feed", description = "Endpoints for managing activity feed entries")
@RestController
@RequestMapping("/activity")
public class ActivityFeedController {
    @Autowired
    private ActivityFeedWebsocket activityFeedWebsocket;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private GroupRepository groupRepo;

    @Autowired
    private ActivityFeedRepository feedRepo;

    @Autowired
    private ImageGalleryRepository imageGalleryRepo;

    private final Logger logger = LoggerFactory.getLogger(ActivityFeedController.class);

    public static class FoodActivityRequest {
        private String food;
        private String mealType;
        private String nutritionInfo;

        public String getFood() { return food; }
        public void setFood(String food) { this.food = food; }
        public String getMealType() { return mealType; }
        public void setMealType(String mealType) { this.mealType = mealType; }
        public String getNutritionInfo() { return nutritionInfo; }
        public void setNutritionInfo(String nutritionInfo) { this.nutritionInfo = nutritionInfo; }
    }

    public static class GoalActivityRequest {
        private String goalDescription;

        public String getGoalDescription() { return goalDescription; }
        public void setGoalDescription(String goalDescription) { this.goalDescription = goalDescription; }
    }

    public static class AchievementActivityRequest {
        private String achievement;

        public String getAchievement() { return achievement; }
        public void setAchievement(String achievement) { this.achievement = achievement; }
    }

    public static class GroupActivityRequest {
        private String updateMessage;
        private String details;

        public String getUpdateMessage() { return updateMessage; }
        public void setUpdateMessage(String updateMessage) { this.updateMessage = updateMessage; }
        public String getDetails() { return details; }
        public void setDetails(String details) { this.details = details; }
    }

    public static class ImageActivityRequest {
        private String image;
        private String caption;

        public ImageActivityRequest() {}

        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
        public String getCaption() { return caption; }
        public void setCaption(String caption) { this.caption = caption; }
    }
    @Operation(
            summary = "Create a food-related activity",
            description = "Creates an activity entry when a user logs their food intake"
    )
    @PostMapping("/food/{userId}/{groupId}")
    public ResponseEntity<ActivityFeed> createFoodActivity(
            @PathVariable int userId,
            @PathVariable int groupId,
            @RequestBody FoodActivityRequest request) {

        User user = userRepo.findById(userId).orElse(null);
        Group group = groupRepo.findById(groupId).orElse(null);

        if (user == null || group == null) {
            return ResponseEntity.badRequest().build();
        }

        String message = String.format("%s logged %s for %s",
                user.getFName(), request.getFood(), request.getMealType());

        // Create and save activity
        ActivityFeed activity = new ActivityFeed(
                message,
                "food eaten",
                user,
                new Timestamp(System.currentTimeMillis()),
                request.getNutritionInfo(),
                group
        );

        activity = feedRepo.save(activity);

        activityFeedWebsocket.broadcastActivity(activity);

        return ResponseEntity.ok(activity);
    }

    @Operation(
            summary = "Create a goal-related activity",
            description = "Creates an activity entry when a user sets a new goal"
    )
    @PostMapping("/goal/{userId}/{groupId}")
    public ResponseEntity<ActivityFeed> createGoalActivity(
            @PathVariable int userId,
            @PathVariable int groupId,
            @RequestBody GoalActivityRequest request) {

        User user = userRepo.findById(userId).orElse(null);
        Group group = groupRepo.findById(groupId).orElse(null);

        if (user == null || group == null) {
            return ResponseEntity.badRequest().build();
        }

        String message = user.getFName() + " set a new goal";

        ActivityFeed activity = activityFeedWebsocket.createAndBroadcastActivity(
                message,
                "goal update",
                user,
                request.getGoalDescription(),
                group
        );

        return ResponseEntity.ok(activity);
    }

    @Operation(
            summary = "Create an achievement-related activity",
            description = "Creates an activity entry when a user earns an achievement"
    )
    @PostMapping("/achievement/{userId}/{groupId}")
    public ResponseEntity<ActivityFeed> createAchievementActivity(
            @PathVariable int userId,
            @PathVariable int groupId,
            @RequestBody AchievementActivityRequest request) {

        User user = userRepo.findById(userId).orElse(null);
        Group group = groupRepo.findById(groupId).orElse(null);

        if (user == null || group == null) {
            return ResponseEntity.badRequest().build();
        }

        String message = user.getFName() + " earned a badge!";

        ActivityFeed activity = activityFeedWebsocket.createAndBroadcastActivity(
                message,
                "achievement",
                user,
                request.getAchievement(),
                group
        );

        return ResponseEntity.ok(activity);
    }

    @Operation(
            summary = "Create a group-related activity",
            description = "Creates an activity entry for group updates"
    )
    @PostMapping("/group/{userId}/{groupId}")
    public ResponseEntity<ActivityFeed> createGroupActivity(
            @PathVariable int userId,
            @PathVariable int groupId,
            @RequestBody GroupActivityRequest request) {

        User user = userRepo.findById(userId).orElse(null);
        Group group = groupRepo.findById(groupId).orElse(null);

        if (user == null || group == null) {
            return ResponseEntity.badRequest().build();
        }

        ActivityFeed activity = activityFeedWebsocket.createAndBroadcastActivity(
                request.getUpdateMessage(),
                "group update",
                user,
                request.getDetails(),
                group
        );

        return ResponseEntity.ok(activity);
    }

    @PostMapping("/image/{userId}/{groupId}")
    public ResponseEntity<ActivityFeed> createImageActivity(
            @PathVariable int userId,
            @PathVariable int groupId,
            @RequestBody ImageActivityRequest request) {

        User user = userRepo.findById(userId).orElse(null);
        Group group = groupRepo.findById(groupId).orElse(null);

        if (user == null || group == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            ActivityFeed activity = new ActivityFeed(
                    user.getFName() + " shared an image",
                    "group update",
                    user,
                    new Timestamp(System.currentTimeMillis()),
                    request.getCaption(),
                    group
            );
            activity.setType(ActivityFeed.ActivityType.GROUP_UPDATE);

            ImageGallery imageGallery = new ImageGallery(request.getImage());
            activity.getImages().add(imageGallery);

            activity = feedRepo.save(activity);

            activityFeedWebsocket.broadcastActivity(activity);

            return ResponseEntity.ok(activity);
        } catch (Exception e) {
            logger.error("Error creating image activity", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

