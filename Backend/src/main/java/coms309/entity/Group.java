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

    public Boolean addMember(GroupMember mem) {
        return members.add(mem);
    }

    public Boolean removeMember(GroupMember mem){
        return members.remove(mem);
    }

    public GroupMember findMember(int uid){
        for(GroupMember mem : members){
            User memUser = mem.getUser();
            if (memUser.getUid() == uid){
                return mem;
            }
        }
        return  null;
    }

    public Boolean isOwnerLevel(String sessionToken){
        String[] array = sessionToken.split(":", 3);
        int accType = Integer.parseInt(array[1].trim());
        int uid = Integer.parseInt(array[2].trim());
        if (accType == 2) {
            return true;
        }
        GroupMember mem = findMember(uid);
        if (mem != null && mem.getPermissionLvl() == 2){
            return true;
        }
        return false;
    }

    public Boolean isModLevel(String sessionToken){
        String[] array = sessionToken.split(":", 3);
        int accType = Integer.parseInt(array[1].trim());
        int uid = Integer.parseInt(array[2].trim());
        if (accType == 2) {
            return true;
        }
        GroupMember mem = findMember(uid);
        if (mem != null && mem.getPermissionLvl() >= 1){
            return true;
        }
        return false;
    }
}
