package coms309.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;


@Entity
@Table(name="group_members")
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="groupmemberid")
    private int id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "joinDate")
    private Timestamp joinDate;


    public GroupMember() {}

    public Group getGroup() {return group;}
    public User getUser() {return user;}
    public int getId() {return id;}

    public Timestamp getJoinDate() {return joinDate;}

    public void setGroup(Group group) {this.group = group;}
    public void setUser(User user) {this.user = user;}
    public void setJoinDate(Timestamp time) {this.joinDate = time;}
}
