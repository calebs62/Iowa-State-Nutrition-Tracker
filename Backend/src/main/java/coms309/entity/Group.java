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

    @OneToMany
    @JoinColumn(name="groupMembers")
    private Set<GroupMember> members;

    @ManyToOne
    @JoinColumn(name="foodPlan")
    private FoodPlan plan;

    public Group() {};

    public int getId() {return id;}
    public String getGroupName() {return groupName;}
    public Set<GroupMember> getMembers() {return members;}
    public FoodPlan getPlan() {return plan;}

    public void setName(String name) {this.groupName = name;}
    public void setMembers(Set<GroupMember> members) {this.members = members;}
    public void setPlan(FoodPlan plan) {this.plan = plan;}

    public Set<GroupMember> addMember(GroupMember mem) {
        members.add(mem);
        return this.members;
    }

}
