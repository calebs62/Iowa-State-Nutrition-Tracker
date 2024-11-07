package com.example.a1_jubair_6_frontend.models;

import java.sql.Timestamp;

public class GroupMember {
    private int id;
    private Group group;
    private User user;
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
