package com.example.a1_jubair_6_frontend.models;

public class MenuItem {
    private int id;
    private int menu;
    private int fooditem;

    public MenuItem() {}
    public MenuItem(int menu, int fooditem) {
        this.menu = menu;
        this.fooditem = fooditem;
    }
    public int getId() {return id;}
    public int getMenu() {return menu;}
    public int getFoodItem() {return fooditem;}
}
