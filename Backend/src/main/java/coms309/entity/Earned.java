package coms309.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "earned")
public class Earned {
    @EmbeddedId
    @Column(name = "earnedId")
    @JsonView(Views.Public.class)
    private EarnedKey id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "earnDate")
    @JsonView(Views.Public.class)
    private Date earnDate = new Date();

    @ManyToOne
    @JoinColumn(name = "user")
    @JsonView(Views.Earned.class)
    private User user;

    @ManyToOne
    @JoinColumn(name = "achievement")
    @JsonView(Views.Earned.class)
    private Achievement achievement;

    @Column(name = "progress")
    @JsonView(Views.Public.class)
    private Integer progress;

    @Column(name = "goal")
    @JsonView(Views.Public.class)
    private Integer goal;

    @Column(name = "has_earned")
    @JsonView(Views.Public.class)
    private Boolean hasEarned;

    public Earned() {}

    public Earned(User user, Achievement achievement){
        this.id = new EarnedKey(user.getUid(), achievement.getId());
        this.user = user;
        this.achievement = achievement;
        this.progress = 0;
        this.hasEarned = false;
        this.goal = achievement.getGoal();
    }

    public EarnedKey getId() {
        return id;
    }

    public Date getEarnDate() {
        return earnDate;
    }

    public void setEarnDate(Date earnDate) {
        this.earnDate = earnDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }

    public Integer getProgress(){
        return this.progress;
    }
    public void setProgress(Integer count){
        progress = count;
    }
    public void addProgress(){
        progress += 1;
    }

    public Boolean getHasEarned() {
        return hasEarned;
    }

    public void setHasEarned(Boolean hasEarned) {
        this.hasEarned = hasEarned;
    }

    @Override
    public String toString() {
        return "Earned{" +
                "id=" + id +
                ", earnDate=" + earnDate +
                ", user=" + user +
                ", achievement=" + achievement +
                '}';
    }

    public Integer getGoal() {
        return goal;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }
}
