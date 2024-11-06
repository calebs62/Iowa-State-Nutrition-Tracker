package coms309.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String userName;

    @Lob
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent")
    private Date sent = new Date();

    public Message(){};

    public Message(String userName, String content){
        this.userName = userName;
        this.content = content;
    }

    public int getId(){return id;}
    public String getContent() {
        return content;
    }
    public String getUserName() {
        return userName;
    }
    public Date getSent() {
        return sent;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setSent(Date sent) {
        this.sent = sent;
    }



}
