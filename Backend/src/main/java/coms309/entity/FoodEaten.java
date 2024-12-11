package coms309.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;

import java.time.LocalDateTime;


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
    private LocalDateTime date = LocalDateTime.now();

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
    public LocalDateTime getDate() {return date;}
    public Integer getUserId(){
        return user != null ? user.getUid() : null;
    }
    public Integer getFoodItemId(){
        return food != null ? food.getId() : null;
    }

    public void setUser(User user) {this.user = user;}
    public void setFood(FoodItem food) {this.food = food;}
    public void setServings(int servings) {this.servings = servings;}
    public void setDate(LocalDateTime date) {this.date = date;}


}
