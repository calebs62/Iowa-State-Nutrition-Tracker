package coms309.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;

import java.util.Date;


@Entity
@Table(name="foodEaten")
public class FoodEaten {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    @JsonView(value = {Views.Public.class})
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dateConsumed")
    @JsonView(value = {Views.Public.class})
    private Date date = new Date();

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonView(value = {Views.FoodEaten.class})
    private User user;

    @ManyToOne
    @JoinColumn(name = "foodItem_id")
    @JsonView(value = {Views.FoodEaten.class})
    private FoodItem food;

    @Column(name="servings")
    @JsonView(value = {Views.Public.class})
    private int servings;

    public FoodEaten() {}

    public FoodEaten(User user, FoodItem foodItem, int servings) {
        this.user = user;
        this.food = foodItem;
        this.servings = servings;
    }

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
