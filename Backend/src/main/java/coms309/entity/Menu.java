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
    private String location = "*";

    @Column(name = "meal")
    private String meal = "*"; //Breakfast/Lunch/Dinner

    @Column(name = "date")
    private Timestamp date = Timestamp.valueOf("2000-01-01");

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

    public void setId(int id) {this.id = id;}
    public void setLocation(String location) {this.location = location;}
    public void setMeal(String meal) {this.meal = meal;}
    public void setDate(Timestamp date) {this.date = date;}

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
                ", date=" + date +
                '}';
    }
}
