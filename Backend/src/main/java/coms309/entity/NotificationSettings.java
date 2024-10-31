package coms309.entity;

import jakarta.persistence.*;

@Entity
@Table(name="notification_settings")
public class NotificationSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int uid;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="user")
    private User user;

    @Column(name = "timenotif")
    private boolean timeNotification = true;

    @Column(name = "systemnotif")
    private boolean systemNotification = true;

    public void setTimeNotification() {timeNotification = !timeNotification;}
    public void setSystemNotification() {systemNotification = !systemNotification;}

    public User getUser() {return user;}
    public boolean getTimeNotification() {return timeNotification;}
    public boolean getSystemNotification() {return systemNotification;}

}
