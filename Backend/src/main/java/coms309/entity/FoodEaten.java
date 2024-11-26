package coms309.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;


@Entity
@Table(name="foodEaten")
public class FoodEaten {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dateConsumed")
    private Date date = new Date();

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
    public Date getDate() {return date;}

    public void setUser(User user) {this.user = user;}
    public void setFood(FoodItem food) {this.food = food;}
    public void setServings(int servings) {this.servings = servings;}
    public void setDate(Date date) {this.date = date;}


}
