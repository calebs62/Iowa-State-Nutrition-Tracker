package coms309.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "activity_feed")
public class ActivityFeed implements Comparable<ActivityFeed>{



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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Group group;
    @OneToMany
    private List<ImageGallery> images = new ArrayList<>();

    @OneToMany
    private List<ImageGallery> images = new ArrayList<>();

    public ActivityFeed() {
        type = null;
        message = "test";
        user = null;
        timestamp = new Timestamp(System.currentTimeMillis());
        additionalData = "";
        group = null;
    }

    public ActivityFeed(String m, String ty, User u, Timestamp time, String ad, Group g) {
        setType(ty);
        message = m;
        user = u;
        if (timestamp == null) {
            timestamp = new Timestamp(System.currentTimeMillis());
        }
        else {
            timestamp = time;
        }
        additionalData = ad;
        group = g;
    }


    public int getId() {return id;}
    public ActivityType getType() {return type;}
    public String getMessage() {return message;}
    public User getUser() {return user;}
    public Timestamp getTimestamp() {return timestamp;}
    public String getAdditionalData() {return additionalData;}
    public Group getGroup() {return group;}
    public List<ImageGallery> getImages() {return images;}

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
    public void setType(ActivityType t) {
        this.type = t;
    }
    public void setUser(User u) {user = u;}
    public void setMessage(String m) {message = m;}
    public void setTimestamp(Timestamp t) {timestamp = t;}
    public void setAdditionalData(String a) {additionalData = a;}
    public void setGroup(Group g) {group = g;}
    public void setImages(List<ImageGallery> img) {images = img;}

    @Override
    public int compareTo(ActivityFeed o) {
        if (this.timestamp.before(o.getTimestamp())) {
            return 1;
        }
        else if(this.timestamp.after(o.getTimestamp())) {
            return -1;
        }
        else {
            return 0;
        }
    }

    public String toString() {
        String ret = "";
        ret += "[" + timestamp.toLocalDateTime().toString() + "]";
        ret += "[" + type.toString() + "]";
        ret += " " + message;
        return ret;
    }

}
