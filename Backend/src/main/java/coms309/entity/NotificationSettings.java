package coms309.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "notification_settings")
public class NotificationSettings {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "user")
    private int user;

    @Column(name = "user_uid")
    private int userUid;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "systembit")
    private boolean system = true;

    @Column(name = "pushbit")
    private boolean push = true;

    @Column(name = "reminderbit")
    private boolean reminder = true;

    @Column(name = "smsbit")
    private boolean sms = true;

    @Column(name = "emailbit")
    private boolean email = true;

    @Column(name = "achievementbit")
    private boolean achievement = true;

    @Column(name = "systemnotif")
    private boolean systemnotif = true;

    @Column(name = "timenotif")
    private boolean timenotif = true;

    public NotificationSettings() {
    }

    public NotificationSettings(int userId) {
        this.id = userId;
        this.user = userId;
        this.userUid = userId;
        this.userId = userId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUser() { return user; }
    public void setUser(int user) { this.user = user; }

    public int getUserUid() { return userUid; }
    public void setUserUid(int userUid) { this.userUid = userUid; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public boolean getSystem() { return system; }
    public void setSystem(boolean system) {
        this.system = system;
        this.systemnotif = system;
    }

    public boolean getPush() { return push; }
    public void setPush(boolean push) {
        this.push = push;
        this.timenotif = push;
    }

    public boolean getReminder() { return reminder; }
    public void setReminder(boolean reminder) { this.reminder = reminder; }

    public boolean getSMS() { return sms; }
    public void setSMS(boolean sms) { this.sms = sms; }

    public boolean getEmail() { return email; }
    public void setEmail(boolean email) { this.email = email; }

    public boolean getAchievement() { return achievement; }
    public void setAchievement(boolean achievement) { this.achievement = achievement; }

    public boolean getSystemnotif() { return systemnotif; }
    public void setSystemnotif(boolean systemnotif) {
        this.systemnotif = systemnotif;
        this.system = systemnotif;
    }

    public boolean getTimenotif() { return timenotif; }
    public void setTimenotif(boolean timenotif) {
        this.timenotif = timenotif;
        this.push = timenotif;
    }
}