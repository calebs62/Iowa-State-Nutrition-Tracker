package com.example.a1_jubair_6_frontend.models;

import java.io.Serializable;

public class GroupMemberKey implements Serializable {
    private int userId;
    private int groupId;

    public GroupMemberKey() {}

    public GroupMemberKey(int groupId, int userId) {
        this.groupId = groupId;
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public int getGroupId() {
        return groupId;
    }

    @Override
    public String toString() {
        return "GroupMemberKey{" +
                "userId=" + userId +
                ", groupId=" + groupId +
                '}';
    }
}
