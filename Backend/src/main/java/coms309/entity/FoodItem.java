package coms309.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="food_item")
public class FoodItem {

    // Unique id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfooditem")
    @JsonView(Views.Public.class)
    private int id;

    @Column(name = "foodtype")
    @JsonView(Views.Public.class)
    private String name;

    // Nutrition Info variables
    @Column(name = "calories")
    @JsonView(Views.Public.class)
    private int calories = -1;
    @Column(name = "totalfat")
    @JsonView(Views.Public.class)
    private int totalFat = -1;
    @Column(name = "sodium")
    @JsonView(Views.Public.class)
    private int sodium = -1;
    @Column(name = "carbohydrate")
    @JsonView(Views.Public.class)
    private int carbohydrate = -1;
    @Column(name = "protein")
    @JsonView(Views.Public.class)
    private int protein = -1;
    @Column(name="servingsize")
    @JsonView(Views.Public.class)
    private String servingsize = "";
    @Column(name="description")
    @JsonView(Views.Public.class)
    private String description = "";

    @ManyToMany(mappedBy="foodItems")
    @JsonView(Views.FoodItem.class)
    @JsonIgnore
    private Set<Menu> menus = new HashSet<>();

    @OneToMany(mappedBy = "food")
    @JsonView(Views.FoodItem.class)
    @JsonIgnore
    private Set<FoodEaten> eaten = new HashSet<>();

    public FoodItem() {}

    public FoodItem(String name, int calories, int totalFat, int sodium, int carbohydrate,
                    int protein, String servingsize, String description) {

        this.name = name;
        this.calories = calories;
        this.totalFat = totalFat;
        this.sodium = sodium;
        this.carbohydrate = carbohydrate;
        this.protein = protein;
        this.servingsize = servingsize;
        this.description = description;
    }

    public String getName() { return name;}
    public int getId() {return id;}
    public int getCalories() {return calories;}
    public int getTotalFat() {return totalFat;}
    public int getSodium() {return sodium;}
    public int getCarbohydrate() {return carbohydrate;}
    public int getProtein() {return protein;}
    public String getServingsize() {return servingsize;}
    public String getDescription() {return description;};
    public Set<FoodEaten> getEaten() {
        return eaten;
    }
    public Set<Menu> getMenus() {
        return menus;
    }

    //Setters
    public void setName(String newName) {
        this.name = newName;
    }
    public void setCalories(int calories) {this.calories = calories;}
    public void setTotalFat(int totalfat) {this.totalFat = totalfat;}
    public void setSodium(int sodium) {this.sodium = sodium;}
    public void setCarbohydrate(int carb) {this.carbohydrate = carb;}
    public void setProtein(int protein) {this.protein = protein;}
    public void setServingSize(String ss) {this.servingsize = ss;}
    public void setDescription(String des) {this.description = des;}
    public void setEaten(Set<FoodEaten> eaten) {
        this.eaten = eaten;
    }
    public void setMenus(Set<Menu> menus) {
        this.menus = menus;
    }

    public void setAll(FoodItem other) {
        this.name = other.getName();
        this.calories = other.getCalories();
        this.totalFat = other.getTotalFat();
        this.sodium = other.getSodium();
        this.carbohydrate = other.getCarbohydrate();
        this.protein = other.getProtein();
        this.servingsize = other.getServingsize();
        this.description = other.getDescription();
    }

    @Override
    public String toString() {
        return "Id: " + id +
                "/nName: " + name +
                "/nCalories: " + calories +
                "/nTotal Fat: " + totalFat +
                "/nSodium: " + sodium +
                "/nTotal Carbohydrate: " + carbohydrate +
                "/nProtein: " + protein +
                "/nServing Size: " + servingsize +
                "/nDescription: " + description;
    }
}
