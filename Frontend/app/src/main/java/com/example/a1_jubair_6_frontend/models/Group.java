package com.example.a1_jubair_6_frontend.models;

import java.util.Set;

public class Group {
    private int id;
    private String groupName;
    private Set<GroupMember> members;
    private FoodPlan plan;

    public Group() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public Set<GroupMember> getMembers() {
        return members;
    }

    public FoodPlan getPlan() {
        return plan;
    }

    public void setName(String name) {
        this.groupName = name;
    }

    public void setMembers(Set<GroupMember> members) {
        this.members = members;
    }

    public void setPlan(FoodPlan plan) {
        this.plan = plan;
    }

    public Set<GroupMember> addMember(GroupMember mem) {
        members.add(mem);
        return this.members;
    }
}
