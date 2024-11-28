package coms309.entity;

import com.fasterxml.jackson.annotation.JsonView;
import coms309.repository.GroupRepository;
import coms309.repository.UserRepository;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name="groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idgroup")
    @JsonView(value = {Views.Public.class})
    private int id;

    @Column(name="groupName")
    @JsonView(value = {Views.Public.class})
    private String groupName;

    @Column(name = "groupOwner")
    @JsonView(value = {Views.Public.class})
    private int ownerId;

    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
    @JsonView(value = {Views.Group.class})
    private Set<GroupMember> members = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="foodPlan")
    @JsonView(value = {Views.Group.class})
    private FoodPlan plan;

    public Group() {}

    public Group(String name, int oId, FoodPlan plan) {
        groupName = name;
        ownerId = oId;
        this.plan = plan;
    }

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
        if (members == null) return null;

        for(GroupMember mem : members){
            User memUser = mem.getUser();
            if (memUser.getUid() == uid){
                return mem;
            }
        }
        return null;
    }

    public Boolean isOwnerLevel(String sessionToken){
        try {
            String[] array = sessionToken.split(":", 3);
            int accType = Integer.parseInt(array[1].trim());
            int uid = Integer.parseInt(array[2].trim());

            GroupMember mem = findMember(uid);
            if (mem != null && mem.getPermissionLvl() == 2) {
                return true;
            }

            return accType == 2;
        } catch (Exception e) {
            return false;
        }
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

    public static boolean isUserInAnyGroup(UserRepository userRepo, GroupRepository groupRepo, int userId) {
        List<Group> allGroups = groupRepo.findAll();
        for (Group group : allGroups) {
            if (group.findMember(userId) != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean isUserOwnerOfAnyGroup(GroupRepository groupRepo, int userId) {
        List<Group> allGroups = groupRepo.findAll();
        for (Group group : allGroups) {
            if (group.getOwnerId() == userId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", ownerId=" + ownerId +
                ", members=" + members.toString() +
                ", plan=" + plan +
                '}';
    }
}
