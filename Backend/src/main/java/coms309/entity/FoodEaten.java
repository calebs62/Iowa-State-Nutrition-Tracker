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
    private int user;

    @ManyToOne
    @JoinColumn(name = "food")
    private int food;

    @Column(name="servings")
    private int servings;

    public FoodEaten() {}

    public int getId() {return id;}
    public int getUser() {return user;};
    public int getFood() {return food;};
    public int getServings() {return servings;}

    public void setUser(int user) {this.user = user;}
    public void setFood(int food) {this.food = food;}
    public void setServings(int servings) {this.servings = servings;}


}
