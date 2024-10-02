package coms309.FoodItems;

import jakarta.persistence.*;

@Entity
@Table(name="FoodItem")
public class FoodItem {

    // Unique id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @Column(name = "food_name")
    String name;

    // Nutrition Info variables
    @Column(name = "Calories")
    int calories;
    @Column(name = "")
    int totalFat;
    @Column(name = "")
    int sodium;
    @Column(name = "")
    int carbohydrate;
    @Column(name = "")
    int protein;

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
