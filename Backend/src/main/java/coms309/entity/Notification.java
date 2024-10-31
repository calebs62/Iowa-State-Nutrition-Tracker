package coms309.entity;

import jakarta.persistence.*;

@Entity
@Table(name="notification")
public class Notification {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="notifId")
    private int id;

    @Column(name="type")
    private String type = "";

    @Column(name="header")
    private String header = "";

    @Column(name="body")
    private String body = "";

    public int getId() {return id;}
    public String getType() {return type;}
    public String getHeader() {return header;}
    public String getBody() {return body;}

    public void setType(String type) {this.type = type;}
    public void setHeader(String header) {this.header = header;}
    public void setBody(String body) {this.body = body;}



}
