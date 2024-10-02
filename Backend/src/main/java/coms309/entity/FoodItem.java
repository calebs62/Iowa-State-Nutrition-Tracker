package coms309.entity;

import jakarta.persistence.*;

@Entity
@Table(name="food_item")
public class FoodItem {

    // Unique id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfooditem")
    private int id;

    @Column(name = "foodtype")
    private String name;

    // Nutrition Info variables
    @Column(name = "calories")
    private int calories;
    @Column(name = "totalfat")
    private int totalFat;
    @Column(name = "sodium")
    private int sodium;
    @Column(name = "carbohydrate")
    private int carbohydrate;
    @Column(name = "protein")
    private int protein;
    @Column(name="servingsize")
    private String servingsize;
    @Column(name="description")
    private String description;

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
    public void changeName(String newName) {
        this.name = newName;
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
