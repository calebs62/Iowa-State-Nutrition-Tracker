package coms309.entity;

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
    @Column
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent")
    @JsonView(value = {Views.Message.class})
    private Date sent = new Date();

    //Since we are only letting a user join one group this is fine.
    // If we wanted to allow users in multiple groups we would have to
    // get the two column on embedded id to work.
    @ManyToOne
//    @JoinColumns({
//            @JoinColumn(name = "userId", referencedColumnName = "user_id", insertable = false, updatable = false),
//            @JoinColumn(name = "groupId", referencedColumnName = "group_id", insertable = false, updatable = false)
//    })
    @JoinColumn(name = "userId", referencedColumnName = "user_id")
    @JsonView(value = {Views.Message.class})
    private GroupMember member;

    public Message(){};

    public Message(GroupMember member, String content){
        this.member = member;
        this.userName = member.getUser().getFName();
        this.groupId = member.getId().getGroupId();
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
    public GroupMember getMember(){return member;}
    public int getGroupId(){return groupId;}

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

}
