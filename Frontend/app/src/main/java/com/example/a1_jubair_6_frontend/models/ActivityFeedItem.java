package com.example.a1_jubair_6_frontend.models;

import java.sql.Timestamp;
import java.util.List;

public class ActivityFeedItem {
    public enum ActivityType {
        FOOD_EATEN,
        GROUP_UPDATE,
        ACHIEVEMENT,
        GOAL_UPDATE
    }

    private int id;
    private User user;
    private ActivityType type;
    private String message;
    private Timestamp timestamp;
    private String additionalData;
    private Group group;
    private List<String> images;

    public ActivityFeedItem() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public ActivityType getType() { return type; }
    public void setType(ActivityType type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getAdditionalData() { return additionalData; }
    public void setAdditionalData(String additionalData) { this.additionalData = additionalData; }

    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
}
