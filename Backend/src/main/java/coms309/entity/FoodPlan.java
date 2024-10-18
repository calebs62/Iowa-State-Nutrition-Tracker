package coms309.entity;

import jakarta.persistence.*;

@Entity
@Table(name="food_plan")
public class FoodPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfoodplan")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "calories")
    private int calories = -1;
    @Column(name = "totalFat")
    private int totalFat = -1;
    @Column(name = "sodium")
    private int sodium = -1;
    @Column(name = "carbohydrate")
    private int carbohydrate = -1;
    @Column(name = "protein")
    private int protein = -1;


    public FoodPlan() {}

    public int getId(){return id;}
    public String getName(){return name;}
    public int getCalories() {return calories;}
    public int getTotalFat() {return totalFat;}
    public int getSodium() {return sodium;}
    public int getCarbohydrate() {return carbohydrate;}
    public int getProtein() {return protein;}

    public void setName(String name) {this.name = name;}
    public void setCalories(int val) {this.calories = val;}
    public void setTotalFat(int val) {this.totalFat = val;}
    public void setSodium(int val) {this.sodium = val;}
    public void setCarbohydrate(int val) {this.sodium = val;}
    public void setProtein(int val) {this.protein = val;}
}
