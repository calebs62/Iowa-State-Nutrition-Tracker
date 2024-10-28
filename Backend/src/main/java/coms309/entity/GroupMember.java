package coms309.entity;

import jakarta.persistence.*;

import java.util.Date;


@Entity
@Table(name="group_members")
public class GroupMember {
    protected enum Permission_Level{
        User,
        Moderator,
        Owner
    }

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

    @Column(name = "permission")
    private Permission_Level permissionLvl;

    @Column(name = "joinDate")
    private Date joinDate;


    public GroupMember() {}

    public Group getGroup() {return group;}
    public User getUser() {return user;}
    public int getId() {return id;}
    public int getPermissionLvl(){return permissionLvl.ordinal();}
    public Date getJoinDate() {return joinDate;}

    public void setGroup(Group group) {this.group = group;}
    public void setUser(User user) {this.user = user;}
    public void setJoinDate(Date time) {this.joinDate = time;}
    public void setPermissionUser(){this.permissionLvl = Permission_Level.User;}
    public void setPermissionMod(){this.permissionLvl = Permission_Level.Moderator;}
    public void setPermissionOwner(){this.permissionLvl = Permission_Level.Owner;}
}
