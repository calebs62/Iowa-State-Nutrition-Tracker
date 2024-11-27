package coms309.controller;

import com.fasterxml.jackson.annotation.JsonView;
import coms309.entity.*;
import coms309.repository.FoodPlanRepository;
import coms309.repository.GroupMemberRepository;
import coms309.repository.GroupRepository;
import coms309.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Groups", description = "Group API")
@RestController
public class GroupController {
    @Autowired
    GroupRepository groupRepo;
    @Autowired
    UserRepository userRepo;
    @Autowired
    GroupMemberRepository memberRepo;
    @Autowired
    FoodPlanRepository planRepo;

    // Create
    @Operation(
            summary="Create Group",
            description="Create group object from map. Requires name, ownerId, and planId."
    )
    @PostMapping("/group")
    @JsonView(value = {Views.Group.class})
    public Group createGroup(@Parameter(description = "Map containing key value pairs to build the group. Requires name, ownerId, and planId.")@RequestBody Map<String, Object> map){
        String name = (String) map.get("groupName");
        Integer ownerId = (Integer) map.get("ownerId");
        Integer planId = (Integer) map.get("planId");

        if (name == null || ownerId == null || planId == null) {
            return null;
        }
        FoodPlan plan = planRepo.findById(planId).orElse(null);
        Group group = new Group(name, ownerId, plan);
        User owner = userRepo.findById(ownerId).orElse(null);
        if (plan != null) {
            plan.addGroup(group);
        }
        groupRepo.save(group);
        if (owner != null) {
            GroupMember ownerMem = new GroupMember(group, owner);
            ownerMem.setPermissionOwner();
            memberRepo.save(ownerMem);
        }
        return group;
    }

    // Read
    @Operation(
            summary="Get Group",
            description="Returns a group by given id."
    )
    @GetMapping("/group/{id}")
    @JsonView(value = {Views.Group.class})
    public Group getGroupById(@Parameter(description = "Group id")@PathVariable int id){
        return groupRepo.findById(id).orElse(null);
    }

    // Get Owner
    @Operation(
            summary="Get Owner",
            description="Returns user that owns the group denoted by given id."
    )
    @GetMapping("/group/{id}/getOwner")
    @JsonView(value = {Views.User.class})
    public User getOwner(@Parameter(description = "Group id")@PathVariable int id) {
        Group currGroup = groupRepo.findById(id).orElse(null);
        if (currGroup != null) {
            return userRepo.findById(currGroup.getOwnerId()).orElse(null);
        }
        return null;
    }

    // Update
    @Operation(
            summary="Update Group",
            description="Updates group based on id and map."
    )
    @PutMapping("/group/update/{id}")
    @JsonView(value = {Views.Group.class})
    public Group updateGroup(@Parameter(description = "Group id")@PathVariable int id, @Parameter(description = "Map containing key value pairs corresponding to fields to be changed.")@RequestBody Map<String, Object> map){
        Group currGroup = groupRepo.findById(id).orElse(null);
        if (currGroup != null  && currGroup.isModLevel((String) map.get("sessionToken"))){
            if (map.containsKey("groupName")){
                currGroup.setName((String) map.get("groupName"));
            }
            if (map.containsKey("groupOwner")){
                currGroup.setOwnerId((int) map.get("groupOwner"));
            }
        }
        return currGroup;
    }

    // Mod add member by id
    @Operation(
            summary="Moderator Add Member",
            description="Moderator adds user to group based on uid and group id."
    )
    @PutMapping("/group/{id}/addMember")
    @JsonView(value = {Views.Group.class})
    public Group addMember(@Parameter(description = "Group id")@PathVariable int id, @Parameter(description = "Map containing key value pairs. Requires sessionToken and uid.")@RequestBody Map<String, Object> map){
        Group currGroup = groupRepo.findById(id).orElse(null);
        if (currGroup != null && currGroup.isModLevel((String) map.get("sessionToken"))){
            User user = userRepo.findById((int) map.get("uid")).orElse(null);
            if (user == null || currGroup.findMember((int) map.get("uid")) != null) {
                return currGroup;
            }
            GroupMember member = new GroupMember(currGroup, user);
            memberRepo.save(member);
        }
        return currGroup;
    }

    // Mod remove member by id
    @Operation(
            summary="Moderator Remove Member",
            description="Moderator removes user from the group based on uid and group id."
    )
    @PutMapping("/group/{id}/removeMember")
    @JsonView(value = {Views.Group.class})
    public Group removeMember(@Parameter(description = "Group id")@PathVariable int id, @Parameter(description = "Map containing key value pairs. Requires sessionToken and uid.")@RequestBody Map<String, Object> map){
        Group currGroup = groupRepo.findById(id).orElse(null);
        if (currGroup != null && currGroup.isModLevel((String) map.get("sessionToken"))){
            GroupMember member = currGroup.findMember((int) map.get("uid"));
            if (member == null){
                return currGroup;
            }
            memberRepo.delete(member);
        }
        return currGroup;
    }

    // User join
    @Operation(
            summary="User Join Group",
            description="User joins group based on sessionToken and group id."
    )
    @PutMapping("/group/{id}/join")
    @JsonView(value = {Views.Group.class})
    public Boolean memberJoin(@Parameter(description = "Group id")@PathVariable int id, @Parameter(description = "String holding sessionToken of user.")@RequestBody String sessionToken){
        Group currGroup = groupRepo.findById(id).orElse(null);
        String[] array = sessionToken.split(":");
        int uid = Integer.parseInt(array[2].trim());
        User currUser = userRepo.findById(uid).orElse(null);
        if (currGroup != null && currUser != null) {
            GroupMember groupMember = new GroupMember(currGroup, currUser);
            memberRepo.save(groupMember);
            return true;
        }
        return false;
    }

    // User leave
    @Operation(
            summary="User Leave Group",
            description="User leave group based on sessionToken and group id."
    )
    @PutMapping("/group/{id}/leave")
    @JsonView(value = {Views.Group.class})
    public Boolean memberLeave(@Parameter(description = "Group id")@PathVariable int id, @Parameter(description = "String holding sessionToken of user.")@RequestBody String sessionToken){
        Group currGroup = groupRepo.findById(id).orElse(null);
        String[] array = sessionToken.split(":");
        int uid = Integer.parseInt(array[2]);
        User currUser = userRepo.findById(uid).orElse(null);
        if (currGroup != null && currUser != null) {
            GroupMember groupMember = currGroup.findMember(uid);
            if (groupMember != null) {
                memberRepo.delete(groupMember);
                return true;
            }
        }
        return false;
    }

    // Change group plan
    @Operation(
            summary="Change Group Food Plan",
            description="Changes group's food plan based on plan id and group id."
    )
    @PutMapping("/group/{id}/changePlan")
    @JsonView(value = {Views.Group.class})
    public Group changePlan(@Parameter(description = "Group id")@PathVariable int id, @Parameter(description = "Map containing key value pairs. Requires sessionToken and planId.")@RequestBody Map<String, Object> map){
        Group currGroup = groupRepo.findById(id).orElse(null);
        if (currGroup != null && currGroup.isModLevel((String) map.get("sessionToken"))) {
            FoodPlan plan = planRepo.findById((int) map.get("planId")).orElse(null);
            if (plan != null) {
                FoodPlan oldPlan = currGroup.getPlan();
                oldPlan.removeGroup(currGroup);
                plan.addGroup(currGroup);
                currGroup.setPlan(plan);
                planRepo.save(oldPlan);
                planRepo.save(plan);
                groupRepo.save(currGroup);
            }
        }
        return currGroup;
    }

    // Owner make user mod
    @Operation(
            summary="Promote to Moderator",
            description="Owner promotes user to moderator by map and group id."
    )
    @PutMapping("/group/{id}/promoteMod")
    @JsonView(value = {Views.GroupMember.class})
    public GroupMember promoteUser(@Parameter(description = "Group id")@PathVariable int id, @Parameter(description = "Map containing key value pairs. Requires sessionToken and uid.")@RequestBody Map<String, Object> map){
        Group currGroup = groupRepo.findById(id).orElse(null);
        if (currGroup != null && currGroup.isOwnerLevel((String) map.get("sessionToken"))) {
            GroupMember member = currGroup.findMember((int) map.get("uid"));
            if (member == null || member.getPermissionLvl() >= 1) {
                return null;
            }
            member.setPermissionMod();
            memberRepo.save(member);
            return member;
        }
        return null;
    }

    // Owner demote mod to user
    @Operation(
            summary="Demote Moderator",
            description="Owner demotes moderator to user by group id and map."
    )
    @PutMapping("/group/{id}/demoteMod")
    @JsonView(value = {Views.GroupMember.class})
    public GroupMember demoteUser(@Parameter(description = "Group id")@PathVariable int id, @Parameter(description = "Map containing key value pairs. Requires sessionToken and uid.")@RequestBody Map<String, Object> map){
        Group currGroup = groupRepo.findById(id).orElse(null);
        if (currGroup != null && currGroup.isOwnerLevel((String) map.get("sessionToken"))) {
            GroupMember member = currGroup.findMember((int) map.get("uid"));
            if (member == null || member.getPermissionLvl() == 2) {
                return null;
            }
            member.setPermissionUser();
            memberRepo.save(member);
            return member;
        }
        return null;
    }

    // Owner give user owner
    @Operation(
            summary="Give User Owner",
            description="Owner gives user ownership of group by group id and map."
    )
    @PutMapping("/group/{id}/makeOwner")
    @JsonView(value = {Views.GroupMember.class})
    public GroupMember makeOwner(@Parameter(description = "Group id")@PathVariable int id, @Parameter(description = "Map containing key value pairs. Requires sessionToken and uid.")@RequestBody Map<String, Object> map){
        Group currGroup = groupRepo.findById(id).orElse(null);
        if (currGroup != null && currGroup.isOwnerLevel((String) map.get("sessionToken"))) {
            GroupMember member = currGroup.findMember((int) map.get("uid"));
            if (member == null || member.getPermissionLvl() == 2) {
                return null;
            }
            int ownerId = currGroup.getOwnerId();
            GroupMember owner = currGroup.findMember(ownerId);
            if (owner != null) {
                owner.setPermissionMod();
                memberRepo.save(owner);
            }
            member.setPermissionOwner();
            currGroup.setOwnerId(member.getUser().getUid());
            memberRepo.save(member);
            groupRepo.save(currGroup);
            return member;
        }
        return null;
    }

    // Delete
    @Operation(
            summary="Delete Group",
            description="Deletes group by given id and returns deleted group."
    )
    @DeleteMapping("/group/{id}")
    @JsonView(value = {Views.Group.class})
    public Group delete(@Parameter(description = "Group id")@PathVariable int id, @Parameter(description = "String holding sessionToken of user")@RequestBody String sessionToken){
        Group group = groupRepo.findById(id).orElse(null);
        if (group != null && group.isOwnerLevel(sessionToken)){
            groupRepo.delete(group);
        }
        return group;
    }

    // List
    @Operation(
            summary="Get All Groups",
            description="Returns a list of all groups."
    )
    @GetMapping("/allGroups")
    @JsonView(value = {Views.Group.class})
    public List<Group> getAllGroups(){
        return groupRepo.findAll();
    }

    @Operation(
            summary="Search Groups",
            description="Returns a list of groups with name containing keyword."
    )
    @GetMapping("/searchGroups")
    @JsonView(value = {Views.Group.class})
    public List<Group> searchGroups(@Parameter(description = "String keyword to search by")@RequestParam(defaultValue = "") String keyword){
        List<Group> groups = groupRepo.findAll();
        if (keyword.isEmpty()){
            return groups;
        }
        String key = keyword.toLowerCase();
        for (Group group : groups) {
            String name = group.getGroupName().toLowerCase();
            if (!(name.contains(key))){
                groups.remove(group);
            }
        }
        return groups;
    }

    @Operation(
            summary="Get Group Plan",
            description="Returns group's plan based on group id."
    )
    @GetMapping("group/{id}/getPlan")
    @JsonView(value = {Views.FoodPlan.class})
    public FoodPlan getFoodplan(@Parameter(description = "Group id")@PathVariable int id){
        Group group = groupRepo.findById(id).orElse(null);
        if (group == null) {
            return null;
        }
        return group.getPlan();
    }

}
