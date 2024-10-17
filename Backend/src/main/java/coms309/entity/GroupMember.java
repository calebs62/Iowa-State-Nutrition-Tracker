package coms309.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;


@Entity
@Table(name="group_member")
public class GroupMember {
    @ManyToOne
    @JoinColumn(name = "group_id")
    private int group;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private int user;

    @Column(name = "joinDate")
    private Timestamp joinDate;


    public GroupMember() {}

    public int getGroup() {return group;}
    public int getUser() {return user;}

    public Timestamp getJoinDate() {return joinDate;}

    public void setGroup(int group) {this.group = group;}
    public void setUser(int user) {this.user = user;}
    public void setJoinDate(Timestamp time) {this.joinDate = time;}
}
