package coms309.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name="SystemNotificationQueue")
public class SystemNotificationQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="header")
    private String header = "";

    @Column (name="body", columnDefinition="LONGTEXT")
    private String body = "";

    @Column (name="time")
    private Timestamp time = new Timestamp(System.currentTimeMillis());

    public SystemNotificationQueue(String h, String b) {
        this.header = h;
        this.body = b;
        this.time = new Timestamp(System.currentTimeMillis());
    }
    public SystemNotificationQueue(String h, String b, Timestamp t) {
        this.header = h;
        this.body = b;
        this.time = t;
    }


    public int getId() {return id;}
    public String getHeader() {return header;}
    public String getBody() {return body;}
    public Timestamp getTime() {return time;}

    public void setHeader(String str) {header = str;}
    public void setBody(String str) {body = str;}
    public void setTime(Timestamp newTime) {time = newTime;}
    public void setTimeCurrent() {time = new Timestamp(System.currentTimeMillis());}

    public boolean timeAfter(Timestamp other) {
        return (this.time.after(other));
    }

}
