package coms309.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class EarnedKey implements Serializable {
    @Column(name = "userId")
    @JsonView(value = {Views.Public.class})
    private int userId;

    @Column(name = "achievementId")
    @JsonView(value = {Views.Public.class})
    private int achievementId;

    public EarnedKey(){}

    public EarnedKey(int userId, int achievementId){
        this.userId = userId;
        this.achievementId = achievementId;
    }

    public int getUserId() {
        return userId;
    }

    public int getAchievementId() {
        return achievementId;
    }

    @Override
    public String toString() {
        return "EarnedKey{" +
                "userId=" + userId +
                ", achievementId=" + achievementId +
                '}';
    }
}
