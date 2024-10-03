package com.example.a1_jubair_6_frontend.models;

import java.sql.Timestamp;

public class Menu {
    private int id;
    private String location;
    private String meal; //Breakfast/Lunch/Dinner
    private Timestamp date;

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
}
