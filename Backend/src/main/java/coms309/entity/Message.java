package coms309.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(value = {Views.Public.class})
    private int id;

    @Column
    @JsonView(value = {Views.Message.class})
    private String userName;

    @Column
    @JsonView(value = {Views.Message.class})
    private int groupId;

    @Lob
    @JsonView(value = {Views.Message.class})
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent")
    @JsonView(value = {Views.Message.class})
    private Date sent = new Date();

    @ManyToOne
//    @JoinColumns({@JoinColumn(name = "userId", insertable = false, updatable = false), @JoinColumn(name = "groupId", insertable = false, updatable = false)})
    @JoinColumn(name = "groupmemberid")
    @JsonView(value = {Views.Message.class})
    private GroupMember member;

//    @OneToMany(mappedBy = "parent")
//    @JsonView(value = {Views.Message.class})
//    @JsonManagedReference
//    private Set<Message> replies = new HashSet<>();
//
//    @ManyToOne
//    @JoinColumn(name = "parent")
//    @JsonView(value = {Views.Message.class})
//    @JsonBackReference
//    private Message parent;

    public Message(){};

    public Message(GroupMember member, String content){
        this.member = member;
        this.userName = member.getUser().getFName();
        this.groupId = member.getId().getGroupId();
        this.content = content;
    }

//    public Message(String userName, String content, Message parent){
//        this.userName = userName;
//        this.content = content;
//        this.parent = parent;
//    }

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
    public GroupMember getMember(){return member;}
    public int getGroupId(){return groupId;}
//    public Message getParent(){
//        return parent;
//    }
//    public Set<Message> getReplies(){
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
    public void setMember(GroupMember member){this.member = member;}
//    public void setParent(Message parent){
//        this.parent = parent;
//    }

}
