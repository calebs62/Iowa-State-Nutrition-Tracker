package coms309.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class GroupMemberKey implements Serializable {
    @Column(name = "userId")
    private int userId;

    @Column(name = "groupId")
    private int groupId;

    public GroupMemberKey(){}

    public int getUserId(){
        return userId;
    }

    public int getGroupId() {
        return groupId;
    }
}
