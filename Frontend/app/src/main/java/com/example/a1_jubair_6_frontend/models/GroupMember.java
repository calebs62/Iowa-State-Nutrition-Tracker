package com.example.a1_jubair_6_frontend.models;

import java.sql.Timestamp;

public class GroupMember {
    private GroupMemberKey id;
    private Group group;
    private User user;
    private int permissionLvl;
    private Timestamp joinDate;

    public GroupMember() {}

    public GroupMember(Group group, User user) {
        this.id = new GroupMemberKey(group.getId(), user.getId());
        this.group = group;
        this.user = user;
        this.permissionLvl = 0;
    }

    public GroupMemberKey getId() { return id; }
    public void setId(GroupMemberKey id) { this.id = id; }

    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getPermissionLvl() { return permissionLvl; }
    public void setPermissionLvl(int permissionLvl) { this.permissionLvl = permissionLvl; }

    public String getPermissionLvlString() {
        if (getPermissionLvl() == 1) {
            return "Moderator";
        }
        else if (getPermissionLvl() == 2) {
            return "Owner";
        }
        else if (getPermissionLvl() == 3) {
            return "Contributor";
        }
        else if (getPermissionLvl() == 4) {
            return "Administrator";
        }
        else {
            return "User";
        }
    }

    public Timestamp getJoinDate() { return joinDate; }
    public void setJoinDate(Timestamp joinDate) { this.joinDate = joinDate; }
}
