package coms309.entity;

import jakarta.persistence.*;

@Entity
@Table(name="privacy_settings")
public class PrivacySettings {
    @Id
    @Column(name="privid")
    private int id;

    @OneToOne
    @MapsId
    @JoinColumn(name= "user_id")
    private User userId;

    @Column(name = "food")
    private boolean food = true;
    @Column(name = "goal")
    private boolean goal = true;
    @Column(name = "achievement")
    private boolean achievement = true;

    public PrivacySettings(int id, User us) {
        this.id = id;
        this.userId = us;
    }

    public User getUserId() {return userId;}

    public User getUser() {return userId;}

    public boolean getFood() {return food;}
    public boolean getGoal() {return goal;}
    public boolean getAchievement() {return achievement;}

    public void setFood(boolean f) {food = f;}
    public void setGoal(boolean g) {goal = g;}
    public void setAchievement(boolean a) {achievement = a;}

}
