package coms309.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="group_members")
public class GroupMember {
    protected enum Permission_Level{
        User,
        Moderator,
        Owner
    }

    @EmbeddedId
    @JsonView(value = {Views.Public.class})
    private GroupMemberKey id;

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    @JsonView(value = {Views.GroupMember.class})
    private Group group;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @JsonView(value = {Views.GroupMember.class})
    private User user;

    @Column(name = "permission")
    @JsonView(value = {Views.Public.class})
    private Permission_Level permissionLvl;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "joinDate")
    @JsonView(value = {Views.Public.class})
    private Date joinDate = new Date();

    @OneToMany(
            mappedBy = "member",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonView(value = {Views.GroupMember.class})
    private List<Message> messages = new ArrayList<>();

    public GroupMember() {}

    public GroupMember(Group group, User user) {
        this.id = new GroupMemberKey(group.getId(), user.getUid());
        this.group = group;
        this.user = user;
        this.permissionLvl = Permission_Level.User;
        this.messages = new ArrayList<>();
    }

    public Group getGroup() {return group;}
    public User getUser() {return user;}
    public GroupMemberKey getId() {return id;}
    public int getPermissionLvl(){return permissionLvl.ordinal();}
    public Date getJoinDate() {return joinDate;}
    public List<Message> getMessages(){return messages;}

    public void setGroup(Group group) {this.group = group;}
    public void setUser(User user) {this.user = user;}
    public void setJoinDate(Date time) {this.joinDate = time;}
    public void setPermissionUser(){this.permissionLvl = Permission_Level.User;}
    public void setPermissionMod(){this.permissionLvl = Permission_Level.Moderator;}
    public void setPermissionOwner(){this.permissionLvl = Permission_Level.Owner;}

    @Override
    public String toString() {
        return "GroupMember{" +
                "id=" + id +
                ", groupName=" + group.getGroupName() +
                ", userName=" + user.getFName() + " "  + user.getLName()+
                ", permissionLvl=" + permissionLvl +
                ", joinDate=" + joinDate +
                '}';
    }
}
