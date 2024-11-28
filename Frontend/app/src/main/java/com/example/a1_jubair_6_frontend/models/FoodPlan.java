package com.example.a1_jubair_6_frontend.models;

public class FoodPlan {
    private int id;
    private String name;
    private int calories = -1;
    private int totalFat = -1;
    private int sodium = -1;
    private int carbohydrate = -1;
    private int protein = -1;

    public FoodPlan() {}

    public int getId(){return id;}
    public String getName(){return name;}
    public int getCalories() {return calories;}
    public int getTotalFat() {return totalFat;}
    public int getSodium() {return sodium;}
    public int getCarbohydrate() {return carbohydrate;}
    public int getProtein() {return protein;}

    public void setId(int id) {this.id = id;}
    public void setName(String name) {this.name = name;}
    public void setCalories(int val) {this.calories = val;}
    public void setTotalFat(int val) {this.totalFat = val;}
    public void setSodium(int val) {this.sodium = val;}
    public void setCarbohydrate(int val) {this.carbohydrate = val;}
    public void setProtein(int val) {this.protein = val;}

}
