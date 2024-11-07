package coms309.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.*;

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
/*
    @OneToMany(mappedBy = "parent")
    @JsonManagedReference
    private List<Message> replies = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "parent")
//    @JsonBackReference
    private Message parent;
//*/
    public Message(){};

    public Message(String userName, String content, Message parent){
        this.userName = userName;
        this.content = content;
//        this.parent = parent;
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
//    public Message getParent(){
//        return parent;
//    }
//    public List<Message> getReplies(){
//        return replies;
//    }

    public void setContent(String content) {
        this.content = content;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setSent(Date sent) {
        this.sent = sent;
    }
//    public void setParent(Message parent){
//        this.parent = parent;
//    }

}
