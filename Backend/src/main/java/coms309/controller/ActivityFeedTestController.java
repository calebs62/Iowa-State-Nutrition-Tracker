package coms309.controller;

import coms309.entity.ActivityFeed;
import coms309.entity.Group;
import coms309.entity.User;
import coms309.repository.GroupRepository;
import coms309.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@Tag(name="Activity Feed Test", description="Used to test activity feed functions")
@RestController
public class ActivityFeedTestController {
    @Autowired
    private ActivityFeedWebsocket activityFeedWebsocket;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private GroupRepository groupRepo;

    private final Random random = new Random();

    private String[] foods = {
            "Grilled Chicken Salad",
            "Protein Smoothie",
            "Quinoa Bowl",
            "Greek Yogurt with Berries",
            "Salmon with Vegetables",
            "Turkey Sandwich",
            "Overnight Oats",
            "Rice and Chicken Bowl",
            "Protein Bar"
    };

    private String[] mealTypes = {
            "breakfast",
            "lunch",
            "dinner",
            "snack"
    };

    private String getRandomFood() {
        return foods[random.nextInt(foods.length)];
    }

    private String getRandomMealType() {
        return mealTypes[random.nextInt(mealTypes.length)];
    }

    private String generateNutritionInfo() {
        int calories = 300 + random.nextInt(700);
        int protein = 15 + random.nextInt(35);
        int carbs = 20 + random.nextInt(60);
        int fat = 5 + random.nextInt(25);

        return String.format("Calories: %d • Protein: %dg • Carbs: %dg • Fat: %dg",
                calories, protein, carbs, fat);
    }

    @Operation(
            summary="Used to test sending a number of ActivityFeed functions",
            description="Input the userId, groupId, and type of activity. Will output activity to activity feed."
    )
    @GetMapping("/test/activity/{userId}/{groupId}/{type}")
    public String createTestActivity(
            @PathVariable int userId,
            @PathVariable int groupId,
            @PathVariable String type) {

        User user = userRepo.findById(userId).orElse(null);
        Group group = groupRepo.findById(groupId).orElse(null);

        if (user == null || group == null) {
            return "User or group not found";
        }

        String message = "";
        String additionalData = "";

        switch (type.toLowerCase()) {
            case "food":
                String food = getRandomFood();
                String mealType = getRandomMealType();
                message = String.format("%s logged %s for %s",
                        user.getFName(), food, mealType);
                additionalData = generateNutritionInfo();
                type = "food eaten";
                break;
            case "goal":
                message = user.getFName() + " set a new goal";
                additionalData = "Target: Walk 10,000 steps daily";
                type = "goal update";
                break;
            case "achievement":
                message = user.getFName() + " earned a badge!";
                additionalData = "First Week Completed!";
                type = "achievement";
                break;
            case "group":
                message = "Group plan updated";
                additionalData = "New weekly meal plan available";
                type = "group update";
                break;
            default:
                return "Invalid activity type";
        }

        ActivityFeed activity = activityFeedWebsocket.createAndBroadcastActivity(
                message,
                type,
                user,
                additionalData,
                group
        );

        return "Created activity: " + activity.getMessage() + "\nAdditional Data: " + activity.getAdditionalData();
    }
}