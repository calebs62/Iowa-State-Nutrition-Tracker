package coms309.entity;

import jakarta.persistence.*;

@Entity
@Table(name="privacy_settings")
public class PrivacySettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="privid")
    private int id;

    @OneToOne
    @MapsId
    @JoinColumn(name= "user_id")
    private User userId;

    @Column(name = "food")
    private boolean system = true;
    @Column(name = "goal")
    private boolean push = true;
    @Column(name = "achievement")
    private boolean reminder = true;

    public PrivacySettings(User us) {
        this.userId = us;
    }

    public User getUserId() {return userId;}

    public boolean getSystem() {return system;}
    public boolean getPush() {return push;}
    public boolean getReminder() {return reminder;}

    public void setSystem(boolean sys) {system = sys;}
    public void setPush(boolean pu) {push = pu;}
    public void setReminder(boolean rem) {reminder = rem;}

}
