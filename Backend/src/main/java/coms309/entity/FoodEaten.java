package coms309.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.sql.Timestamp;


@Entity
@Table(name="foodEaten")
public class FoodEaten {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;


    @Column(name = "timeConsumed")
    private Timestamp time = new Timestamp(System.currentTimeMillis());

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "foodItem_id")
    private FoodItem food;

    @Column(name="servings")
    private int servings;

    public FoodEaten() {}

    public int getId() {return id;}
    public User getUser() {return user;};
    public FoodItem getFood() {return food;};
    public int getServings() {return servings;}
    public Timestamp getTime() {return time;}

    public void setUser(User user) {this.user = user;}
    public void setFood(FoodItem food) {this.food = food;}
    public void setServings(int servings) {this.servings = servings;}
    public void setTime(Timestamp time) {this.time = time;}


}
