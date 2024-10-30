package coms309.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name="groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idgroup")
    private int id;

    @Column(name="groupName")
    private String groupName;

    @Column(name = "groupOwner")
    private int ownerId;

    @OneToMany
    @JoinColumn(name="groupMembers")
    private Set<GroupMember> members;

    @ManyToOne
    @JoinColumn(name="foodPlan")
    private FoodPlan plan;

    public Group() {};

    public int getId() {return id;}
    public String getGroupName() {return groupName;}
    public int getOwnerId() {
        return ownerId;
    }
    public Set<GroupMember> getMembers() {return members;}
    public FoodPlan getPlan() {return plan;}

    public void setName(String name) {this.groupName = name;}
    public void setOwnerId(int id){ownerId = id;}
    public void setMembers(Set<GroupMember> members) {this.members = members;}
    public void setPlan(FoodPlan plan) {this.plan = plan;}

    public Set<GroupMember> addMember(GroupMember mem) {
        members.add(mem);
        return members;
    }

    public Set<GroupMember> removeMember(GroupMember mem){
        members.remove(mem);
        return members;
    }

    public Boolean isOwnerLevel(int userId){
        return true;
    }

    public Boolean isModLevel(int userId){
        return true;
    }
}
