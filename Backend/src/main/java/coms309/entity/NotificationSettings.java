package coms309.entity;

import jakarta.persistence.*;

@Entity
@Table(name="notification_settings")
public class NotificationSettings {
    @Id
    private int userId;

    @Column(name = "timenotif")
    private boolean timeNotification = true;

    @Column(name = "systemnotif")
    private boolean systemNotification = true;

    @Column(name = "achievement")
    private boolean achievementNotification = true;


    public NotificationSettings(int id) {
        this.userId = id;
    }

    public void setTimeNotification(boolean set) {timeNotification = set;}
    public void setSystemNotification(boolean set) {systemNotification = set;}
    public void setAchievementNotification(boolean set) {achievementNotification = set;}

    public int getUser() {return userId;}
    public boolean getTimeNotification() {return timeNotification;}
    public boolean getSystemNotification() {return systemNotification;}
    public boolean getAchievementNotification() {return achievementNotification;}

}
