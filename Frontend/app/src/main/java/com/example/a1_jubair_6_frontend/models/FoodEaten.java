package com.example.a1_jubair_6_frontend.models;

import androidx.annotation.NonNull;

import java.sql.Timestamp;

public class FoodEaten {
    private int id;
    private Timestamp time;
    private User user;
    private FoodItem food;
    private int servings;

    public FoodEaten() {
        this.time = new Timestamp(System.currentTimeMillis());
    }

    public FoodEaten(User user, FoodItem food, int servings) {
        this.time = new Timestamp(System.currentTimeMillis());
        this.user = user;
        this.food = food;
        this.servings = servings;
    }

    public int getId() {
        return id;
    }

    public Timestamp getTime() {
        return time;
    }

    public User getUser() {
        return user;
    }

    public FoodItem getFood() {
        return food;
    }

    public int getServings() {
        return servings;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFood(FoodItem food) {
        this.food = food;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    @NonNull
    @Override
    public String toString() {
        return "FoodEaten{" +
                "id=" + id +
                ", time=" + time +
                ", user=" + user +
                ", food=" + food +
                ", servings=" + servings +
                '}';
    }
}