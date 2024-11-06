package coms309.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "activity_feed")
public class ActivityFeed {

    public enum ActivityType {
        FOOD_EATEN,
        GROUP_UPDATE,
        ACHIEVEMENT,
        GOAL_UPDATE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private ActivityType type;
    @Column(nullable = false)
    private String message;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "timestamp")
    private Timestamp timestamp;
    @Column(name = "additional_data")
    private String additionalData;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    public int getId() {return id;}
    public ActivityType getType() {return type;}
    public String getMessage() {return message;}
    public User getUser() {return user;}
    public Timestamp getTimestamp() {return timestamp;}
    public String getAdditionalData() {return additionalData;}
    public Group getGroup() {return group;}

    public void setType(String t) {
        if (t.equals("food eaten")) {
            type = ActivityType.FOOD_EATEN;
        }
        else if (t.equals("group update")) {
            type = ActivityType.GROUP_UPDATE;
        }
        else if (t.equals("achievement")) {
            type = ActivityType.ACHIEVEMENT;
        }
        else if (t.equals("goal update")) {
            type = ActivityType.GOAL_UPDATE;
        }
    }

    public void setMessage(String m) {message = m;}
    public void setTimestamp(Timestamp t) {timestamp = t;}
    public void setAdditionalData(String a) {additionalData = a;}
    public void setGroup(Group g) {group = g;}


}
