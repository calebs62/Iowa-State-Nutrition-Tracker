package coms309.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupMemberKey that = (GroupMemberKey) o;
        return userId == that.userId && groupId == that.groupId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, groupId);
    }
}
