package com.example.a1_jubair_6_frontend.models;

import android.util.Log;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

public class Menu {
    private int id;
    private String name;
    private String location;
    private String meal; //Breakfast/Lunch/Dinner
    private Timestamp date;
    private Set<FoodItem> foodItems;

    public Menu() {
        this.foodItems = new HashSet<>();
    }

    public Menu(String location, String meal, Timestamp date) {
        this.location = location;
        this.meal = meal;
        this.date = date;
    }
    public int getId() {return id;}
    public String getName() {
        return name;
    }
    public String getLocation() {return location;}
    public String getMeal() {return meal;}
    public Timestamp getDate() {return date;}
    public Set<FoodItem> getFoodItems() {
        return foodItems != null ? foodItems : new HashSet<>();
    }

    public void setId(int id) {this.id = id;}
    public void setName(String name) {this.name = name;}
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

    public void setDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = sdf.parse(dateStr);
            this.date = new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            Log.e("Menu", "Error parsing date: " + dateStr);
            this.date = new Timestamp(System.currentTimeMillis());
        }
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)",
                location,
                meal,
                new SimpleDateFormat("MM/dd/yyyy").format(date));
    }
}
