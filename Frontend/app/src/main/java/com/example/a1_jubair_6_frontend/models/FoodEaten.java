package com.example.a1_jubair_6_frontend.models;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FoodEaten {
    private int id;

    @SerializedName("date")
    private Date time;

    private User user;
    private FoodItem food;
    private float servings;

    private static final SimpleDateFormat ISO_FORMAT;
    private static final TimeZone CST_TIMEZONE = TimeZone.getTimeZone("America/Chicago");

    static {
        ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        ISO_FORMAT.setTimeZone(CST_TIMEZONE);  // Set to CST since that's what the server uses
    }

    public static Date parseDate(String dateStr) {
        try {
            return ISO_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getTime() { return time; }
    public void setTime(Date time) { this.time = time; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public FoodItem getFood() { return food; }
    public void setFood(FoodItem food) { this.food = food; }

    public float getServings() { return servings; }
    public void setServings(float servings) { this.servings = servings; }
}