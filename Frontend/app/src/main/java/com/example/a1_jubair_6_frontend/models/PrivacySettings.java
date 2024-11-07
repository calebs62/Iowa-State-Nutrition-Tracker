package com.example.a1_jubair_6_frontend.models;

public class PrivacySettings {
    private int id;
    private boolean food;
    private boolean goal;
    private boolean achievement;

    public PrivacySettings() {
        this.food = true;
        this.goal = true;
        this.achievement = true;
    }

    public PrivacySettings(boolean food, boolean goal, boolean achievement) {
        this.food = food;
        this.goal = goal;
        this.achievement = achievement;
    }

    public int getId() { return id; }
    public boolean getFood() { return food; }
    public boolean getGoal() { return goal; }
    public boolean getAchievement() { return achievement; }

    public void setId(int id) { this.id = id; }
    public void setFood(boolean food) { this.food = food; }
    public void setGoal(boolean goal) { this.goal = goal; }
    public void setAchievement(boolean achievement) { this.achievement = achievement; }
}