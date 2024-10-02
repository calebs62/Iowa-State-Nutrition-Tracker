package coms309.FoodItems;

import jakarta.persistence.*;

@Entity
@Table(name="FoodItem")
public class FoodItem {

    // Unique id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfooditem")
    int id;

    @Column(name = "foodtype")
    String name;

    // Nutrition Info variables
    @Column(name = "calories")
    int calories;
    @Column(name = "totalfat")
    int totalFat;
    @Column(name = "sodium")
    int sodium;
    @Column(name = "carbohydrates")
    int carbohydrate;
    @Column(name = "protein")
    int protein;

    @Column(name="description")
    String description;

    public FoodItem() {}
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
    public String toString() {
        return "Id: " + id +
                "/nName: " + name +
                "/nCalories: " + calories +
                "/nTotal Fat: " + totalFat +
                "/nSodium: " + sodium +
                "/nTotal Carbohydrate: " + carbohydrate +
                "/nProtein: " + protein;
    }
}
