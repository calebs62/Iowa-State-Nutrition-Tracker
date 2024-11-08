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
    @Column(name = "user")
    @JsonView(Views.Earned.class)
    private User user;

    @ManyToOne
    @Column(name = "achievement")
    @JsonView(Views.Earned.class)
    private Achievement achievement;

    public Earned() {}

    public Earned(User user, Achievement achievement){
        this.id = new EarnedKey(user.getUid(), achievement.getId());
        this.user = user;
        this.achievement = achievement;
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

    @Override
    public String toString() {
        return "Earned{" +
                "id=" + id +
                ", earnDate=" + earnDate +
                ", user=" + user +
                ", achievement=" + achievement +
                '}';
    }
}
