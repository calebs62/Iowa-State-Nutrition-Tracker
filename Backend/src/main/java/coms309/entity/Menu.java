package coms309.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Set;

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
    private Timestamp date = getDate();

    @ManyToMany(cascade = {
            CascadeType.PERSIST
    })
    @JoinTable(name = "menu_food",
            joinColumns = {@JoinColumn(name = "menu_id")},
            inverseJoinColumns = {@JoinColumn(name = "fooditem_id")})
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
