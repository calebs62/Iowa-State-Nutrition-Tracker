package coms309.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class GroupMemberKey implements Serializable {
    @Column(name = "userId")
    @JsonView(value = {Views.Public.class})
    private int userId;

    @Column(name = "groupId")
    @JsonView(value = {Views.Public.class})
    private int groupId;

    public GroupMemberKey(){}

    public GroupMemberKey(int groupId, int userId){
        this.groupId = groupId;
        this.userId = userId;
    }

    public int getUserId(){
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
