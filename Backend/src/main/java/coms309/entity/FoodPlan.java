package coms309.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;

import java.util.Set;


@Entity
@Table(name="food_plan")
public class FoodPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfoodplan")
    @JsonView(value = {Views.Public.class})
    private int id;

    @Column(name = "name")
    @JsonView(value = {Views.Public.class})
    private String name;

    @Column(name = "calories")
    @JsonView(value = {Views.Public.class})
    private int calories;
    @Column(name = "totalFat")
    @JsonView(value = {Views.Public.class})
    private int totalFat;
    @Column(name = "sodium")
    @JsonView(value = {Views.Public.class})
    private int sodium;
    @Column(name = "carbohydrate")
    @JsonView(value = {Views.Public.class})
    private int carbohydrate;
    @Column(name = "protein")
    @JsonView(value = {Views.Public.class})
    private int protein;

    @OneToMany(mappedBy = "plan")
    @JsonView(value = {Views.FoodPlan.class})
    private Set<Group> groups;

    public FoodPlan() {}

    public int getId(){return id;}
    public String getName(){return name;}
    public int getCalories() {return calories;}
    public int getTotalFat() {return totalFat;}
    public int getSodium() {return sodium;}
    public int getCarbohydrate() {return carbohydrate;}
    public int getProtein() {return protein;}

    public Set<Group> getGroups(){return groups;}

    public void setName(String name) {this.name = name;}
    public void setCalories(int val) {this.calories = val;}
    public void setTotalFat(int val) {this.totalFat = val;}
    public void setSodium(int val) {this.sodium = val;}
    public void setCarbohydrate(int val) {this.carbohydrate = val;}
    public void setProtein(int val) {this.protein = val;}

    public void setGroups(Set<Group> groups){this.groups = groups;}
    public void addGroup(Group group){groups.add(group);}
    public void removeGroup(Group group){groups.remove(group);}
}
