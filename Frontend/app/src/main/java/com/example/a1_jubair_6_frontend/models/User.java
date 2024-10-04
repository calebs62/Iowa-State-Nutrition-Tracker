package com.example.a1_jubair_6_frontend.models;

import android.net.Uri;

import com.example.a1_jubair_6_frontend.utils.ImageUtils;

public class User {
    private String username;
    private String password;
    private String img;
    private String fname;
    private String lname;
    private int height;
    private int weight;
    private Account accounttype; // Account type (USER, CONTRIBUTOR, ADMINISTRATOR)

    public enum Account {
        USER,
        CONTRIBUTOR,
        ADMINISTRATOR
    }

    public User(String username, String password, String fname, String lname, int height, int weight, Account accounttype) {
        this.username = username;
        this.password = password;
        this.fname = fname;
        this.lname = lname;
        this.height = height;
        this.weight = weight;
        this.accounttype = accounttype;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public Account getAccounttype() {
        return accounttype;
    }
}
