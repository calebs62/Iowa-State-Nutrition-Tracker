package coms309.entity;

import jakarta.persistence.*;

@Entity
@Table(name="notification_settings")
public class NotificationSettings {
    @Id
    private int userId;

    @Column(name = "system")
    private boolean system = true;
    @Column(name = "push")
    private boolean push = true;
    @Column(name = "reminder")
    private boolean reminder = true;
    @Column(name = "sms")
    private boolean sms = true;
    @Column(name = "email")
    private boolean email = true;

    public NotificationSettings(int id) {
        this.userId = id;
    }
    public int getUser() {return userId;}
    public boolean getSystem() {return system;}
    public boolean getPush() {return push;}
    public boolean getReminder() {return reminder;}
    public boolean getSMS() {return sms;}
    public boolean getEmail() {return email;}

    public void setSystem(boolean bool) {this.system = bool;}
    public void setPush(boolean bool) {this.push = bool;}
    public void setReminder(boolean bool) {this.reminder = bool;}
    public void setSMS(boolean bool) {this.sms = bool;}
    public void setEmail(boolean bool) {this.email = bool;}

}
