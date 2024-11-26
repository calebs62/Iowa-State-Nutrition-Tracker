package com.example.a1_jubair_6_frontend.models;

import com.google.gson.annotations.SerializedName;

public class FoodItem {
    @SerializedName("id")
    private int idfooditem;

    @SerializedName("name")
    private String foodtype;

    private int calories;

    @SerializedName("totalFat")
    private int totalfat;

    private int sodium;
    private int carbohydrate;
    private int protein;
    private String servingsize;
    private String description;
    private int quantity = 0;

    public FoodItem() {}

    public FoodItem(String name, int calories, int totalFat, int sodium, int carbohydrate,
                    int protein, String servingsize, String description) {
        this.foodtype = name;
        this.calories = calories;
        this.totalfat = totalFat;
        this.sodium = sodium;
        this.carbohydrate = carbohydrate;
        this.protein = protein;
        this.servingsize = servingsize;
        this.description = description;
    }

    // Getters
    public String getName() { return foodtype; }
    public int getId() { return idfooditem; }
    public int getCalories() { return calories; }
    public int getTotalFat() { return totalfat; }
    public int getSodium() { return sodium; }
    public int getCarbohydrate() { return carbohydrate; }
    public int getProtein() { return protein; }
    public String getServingsize() { return servingsize; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }

    // Setters
    public void setId(int id) { this.idfooditem = id; }
    public void setName(String name) { this.foodtype = name; }
    public void setCalories(int calories) { this.calories = calories; }
    public void setTotalFat(int totalFat) { this.totalfat = totalFat; }
    public void setSodium(int sodium) { this.sodium = sodium; }
    public void setCarbohydrate(int carbohydrate) { this.carbohydrate = carbohydrate; }
    public void setProtein(int protein) { this.protein = protein; }
    public void setServingsize(String servingsize) { this.servingsize = servingsize; }
    public void setDescription(String description) { this.description = description; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return String.format("FoodItem{id=%d, name='%s', calories=%d, totalFat=%d, sodium=%d, carbohydrate=%d, protein=%d, servingSize='%s', description='%s', quantity=%d}",
                idfooditem, foodtype, calories, totalfat, sodium, carbohydrate, protein, servingsize, description, quantity);
    }
}
