package com.example.a1_jubair_6_frontend.models;

import java.util.HashSet;
import java.util.Set;

public class Menu {
    private int id;
    private String name;
    private String location;
    private String meal;
    private String date;
    private Set<FoodItem> foodItems;

    public Menu() {
        this.foodItems = new HashSet<>();
    }

    public Menu(String name, String location, String meal, String date) {
        this.name = name;
        this.location = location;
        this.meal = meal;
        this.date = date;
        this.foodItems = new HashSet<>();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getMeal() { return meal; }
    public String getDate() { return date; }
    public Set<FoodItem> getFoodItems() {
        return foodItems != null ? foodItems : new HashSet<>();
    }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }
    public void setMeal(String meal) { this.meal = meal; }
    public void setDate(String date) { this.date = date; }
    public void setFoodItems(Set<FoodItem> foodItems) { this.foodItems = foodItems; }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", location, meal, date);
    }
}
