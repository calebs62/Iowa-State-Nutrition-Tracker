package com.example.a1_jubair_6_frontend.models;

public class PrivacySettings {
    private boolean foodSharingEnabled;
    private boolean goalSharingEnabled;
    private boolean achievementSharingEnabled;

    public PrivacySettings(boolean foodSharingEnabled, boolean goalSharingEnabled,
                           boolean achievementSharingEnabled) {
        this.foodSharingEnabled = foodSharingEnabled;
        this.goalSharingEnabled = goalSharingEnabled;
        this.achievementSharingEnabled = achievementSharingEnabled;
    }

    // Getters and setters
    public boolean isFoodSharingEnabled() { return foodSharingEnabled; }
    public void setFoodSharingEnabled(boolean enabled) { this.foodSharingEnabled = enabled; }

    public boolean isGoalSharingEnabled() { return goalSharingEnabled; }
    public void setGoalSharingEnabled(boolean enabled) { this.goalSharingEnabled = enabled; }

    public boolean isAchievementSharingEnabled() { return achievementSharingEnabled; }
    public void setAchievementSharingEnabled(boolean enabled) { this.achievementSharingEnabled = enabled; }
}
