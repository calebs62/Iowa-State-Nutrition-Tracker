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

    @EmbeddedId
    @Column(name="groupmemberid")
    private GroupMemberKey id;

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "permission")
    private Permission_Level permissionLvl;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "joinDate")
    private Date joinDate = new Date();


    public GroupMember() {}

    public GroupMember(Group group, User user) {
        this.group = group;
        this.user = user;
        this.permissionLvl = Permission_Level.User;
    }

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
