package coms309.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="menu")

public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idmenu")
    @JsonView(Views.Public.class)
    private int id;

    @Column(name = "name")
    @JsonView(Views.Public.class)
    private String name;

    @Column(name = "location")
    @JsonView(Views.Public.class)
    private String location;

    @Column(name = "meal")
    @JsonView(Views.Public.class)
    private String meal; //Breakfast/Lunch/Dinner

    @Column(name = "date")
    @JsonView(Views.Public.class)
    private LocalDate date;

    @ManyToMany(cascade = {
            CascadeType.PERSIST
    }, fetch = FetchType.EAGER)
    @JoinTable(name = "menu_food",
            joinColumns = {@JoinColumn(name = "menu_id")},
            inverseJoinColumns = {@JoinColumn(name = "fooditem_id")})
    @JsonView(Views.Menu.class)
    private Set<FoodItem> foodItems = new HashSet<>();

    public Menu() {}
    public Menu(String name, String location, String meal, String dateString) {
        this.name = name;
        this.location = location;
        this.meal = meal;
        this.date = LocalDate.parse(dateString);
    }
    public int getId() {return id;}
    public String getName() {return name;}
    public String getLocation() {return location;}
    public String getMeal() {return meal;}
    public LocalDate getDate() {return date;}
    public Set<FoodItem> getFoodItems() {return foodItems;}

    public void setId(int id) {this.id = id;}
    public void setName(String name) {this.name = name;};
    public void setLocation(String location) {this.location = location;}
    public void setMeal(String meal) {this.meal = meal;}
    public void setDate(String dateString) {this.date = LocalDate.parse(dateString);}
    public void setFoodItems(Set<FoodItem> foodItems) {this.foodItems = foodItems;}

    public void addFoodItem(FoodItem newItem){
        foodItems.add(newItem);
    }
    public void removeFoodItem(FoodItem delItem){
        foodItems.remove(delItem);
    }

    public void setAll(Menu updatedMenu){
        this.name = updatedMenu.name;
        this.location = updatedMenu.location;
        this.meal = updatedMenu.meal;
        this.date = updatedMenu.date;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", meal='" + meal + '\'' +
                ", date=" + date + '\'' +
                ", foodItems= {" + foodItems + "}" +
                '}';
    }
}
