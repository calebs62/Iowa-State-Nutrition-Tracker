package com.example.a1_jubair_6_frontend.models;

import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Set;

public class Menu {
    private int id;
    private String location;
    private String meal; //Breakfast/Lunch/Dinner
    private Timestamp date;
    private Set<FoodItem> foodItems;

    public Menu() {}
    public Menu(String location, String meal, Timestamp date) {
        this.location = location;
        this.meal = meal;
        this.date = date;
    }
    public int getId() {return id;}
    public String getLocation() {return location;}
    public String getMeal() {return meal;}
    public Timestamp getDate() {return date;}
    public Set<FoodItem> getFoodItems() {return foodItems;}

    public void setId(int id) {this.id = id;}
    public void setLocation(String location) {this.location = location;}
    public void setMeal(String meal) {this.meal = meal;}
    public void setDate(Timestamp date) {this.date = date;}
    public void setFoodItems(Set<FoodItem> foodItems) {this.foodItems = foodItems;}

    public void addFoodItem(FoodItem newItem){
        foodItems.add(newItem);
    }
    public void removeFoodItem(FoodItem delItem){
        foodItems.remove(delItem);
    }

    public void setAll(Menu updatedMenu){
        this.location = updatedMenu.getLocation();
        this.meal = updatedMenu.getMeal();
        this.date = updatedMenu.getDate();
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", location='" + location + '\'' +
                ", meal='" + meal + '\'' +
                ", date=" + date + '\'' +
                ", foodItems= {" + foodItems + "}" +
                '}';
    }
}
