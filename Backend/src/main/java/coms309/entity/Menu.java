package coms309.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name="menu")

public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idmenu")
    private int id;

    @Column(name = "location")
    private String location;

    @Column(name = "meal")
    private String meal; //Breakfast/Lunch/Dinner

    @Column(name = "date")
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
