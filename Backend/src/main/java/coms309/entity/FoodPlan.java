package coms309.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.Set;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
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
    private int calories;
    @Column(name = "totalFat")
    private int totalFat;
    @Column(name = "sodium")
    private int sodium;
    @Column(name = "carbohydrate")
    private int carbohydrate;
    @Column(name = "protein")
    private int protein;

    @OneToMany(mappedBy = "plan")
//    @JsonBackReference
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
