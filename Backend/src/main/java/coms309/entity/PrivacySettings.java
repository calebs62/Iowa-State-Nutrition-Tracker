package coms309.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="privacy_settings")
public class PrivacySettings {
    @Id
    @Column(name="user_id")
    private int userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", referencedColumnName = "UID", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    @Column(name = "food")
    private boolean food = true;
    @Column(name = "goal")
    private boolean goal = true;
    @Column(name = "achievement")
    private boolean achievement = true;

    public PrivacySettings() {
        this.food = true;
        this.goal = true;
        this.achievement = true;
    }

    public PrivacySettings(int userId) {
        this.userId = userId;
        this.food = true;
        this.goal = true;
        this.achievement = true;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public boolean getFood() { return food; }
    public void setFood(boolean food) { this.food = food; }
    public boolean getGoal() { return goal; }
    public void setGoal(boolean goal) { this.goal = goal; }
    public boolean getAchievement() { return achievement; }
    public void setAchievement(boolean achievement) { this.achievement = achievement; }

    @Override
    public String toString() {
        return "PrivacySettings{" +
                "userId=" + userId +
                ", food=" + food +
                ", goal=" + goal +
                ", achievement=" + achievement +
                '}';
    }
}