package coms309.controller;

import coms309.entity.FoodPlan;
import coms309.entity.Group;
import coms309.entity.GroupMember;
import coms309.entity.User;
import coms309.repository.FoodPlanRepository;
import coms309.repository.GroupMemberRepository;
import coms309.repository.GroupRepository;
import coms309.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    @PostMapping("/group")
    public Group createGroup(@RequestBody Map<String, Object> map){
        String name = (String) map.get("groupName");
        Integer ownerId = (Integer) map.get("ownerId");
        Integer planId = (Integer) map.get("planId");

        if (name == null || ownerId == null || planId == null) {
            return null;
        }
        FoodPlan plan = planRepo.findById(planId).orElse(null);
        Group group = groupRepo.save(new Group(name, ownerId, plan));
        User owner = userRepo.findById(ownerId).orElse(null);
        if (owner != null) {
            GroupMember ownerMem = new GroupMember(group, owner);
            ownerMem.setPermissionOwner();
            memberRepo.save(ownerMem);
            owner.addMembered(ownerMem);
            userRepo.save(owner);
        }
        return group;
    }

    // Read
    @GetMapping("/group/{id}")
    public Group getGroupById(@PathVariable int id){
        return groupRepo.findById(id).orElse(null);
    }

    // Get Owner
    @GetMapping("/group/{id}/getOwner")
    public User getOwner(@PathVariable int id) {
        Group currGroup = groupRepo.findById(id).orElse(null);
        if (currGroup != null) {
            return userRepo.findById(currGroup.getOwnerId()).orElse(null);
        }
        return null;
    }

    // Update
    @PutMapping("/group/update/{id}")
    public Group updateGroup(@PathVariable int id, @RequestBody Map<String, Object> newGroup){
        Group currGroup = groupRepo.findById(id).orElse(null);
        if (currGroup != null  && currGroup.isModLevel((String) newGroup.get("sessionToken"))){
            if (newGroup.containsKey("groupName")){
                currGroup.setName((String) newGroup.get("groupName"));
            }
            if (newGroup.containsKey("groupOwner")){
                currGroup.setOwnerId((int) newGroup.get("groupOwner"));
            }
        }
        return currGroup;
    }

    // Mod add member by id
    @PutMapping("/group/{id}/addMember")
    public Group addMember(@PathVariable int id, @RequestBody Map<String, Object> map){
        Group currGroup = groupRepo.findById(id).orElse(null);
        if (currGroup != null && currGroup.isModLevel((String) map.get("sessionToken"))){
            User user = userRepo.findById((int) map.get("uid")).orElse(null);
            if (user == null || currGroup.findMember((int) map.get("uid")) != null) {
                return currGroup;
            }
            GroupMember member = new GroupMember(currGroup, user);
            currGroup.addMember(member);
            user.addMembered(member);
            userRepo.save(user);
            groupRepo.save(currGroup);
            memberRepo.save(member);
        }
        return currGroup;
    }

    // Mod remove member by id
    @PutMapping("/group/{id}/removeMember")
    public Group removeMember(@PathVariable int id, @RequestBody Map<String, Object> map){
        Group currGroup = groupRepo.findById(id).orElse(null);
        if (currGroup != null && currGroup.isModLevel((String) map.get("sessionToken"))){
            GroupMember member = currGroup.findMember((int) map.get("uid"));
            if (member == null){
                return currGroup;
            }
            User user = member.getUser();
            user.removeMembered(member);
            currGroup.removeMember(member);
            userRepo.save(user);
            groupRepo.save(currGroup);
            memberRepo.delete(member);
        }
        return currGroup;
    }

    // User join
    @PutMapping("/group/{id}/join")
    public Boolean memberJoin(@PathVariable int id, @RequestBody String sessionToken){
        Group currGroup = groupRepo.findById(id).orElse(null);
        String[] array = sessionToken.split(":");
        int uid = Integer.parseInt(array[2].trim());
        User currUser = userRepo.findById(uid).orElse(null);
        if (currGroup != null && currUser != null) {
            GroupMember groupMember = new GroupMember(currGroup, currUser);
            currUser.addMembered(groupMember);
            currGroup.addMember(groupMember);
            userRepo.save(currUser);
            groupRepo.save(currGroup);
            memberRepo.save(groupMember);
            return true;
        }
        return false;
    }

    // User leave
    @PutMapping("/group/{id}/leave")
    public Boolean memberLeave(@PathVariable int id, @RequestBody String sessionToken){
        Group currGroup = groupRepo.findById(id).orElse(null);
        String[] array = sessionToken.split(":");
        int uid = Integer.parseInt(array[2]);
        User currUser = userRepo.findById(uid).orElse(null);
        if (currGroup != null && currUser != null) {
            GroupMember groupMember = currGroup.findMember(uid);
            if (groupMember != null) {
                User user = groupMember.getUser();
                user.removeMembered(groupMember);
                currGroup.removeMember(groupMember);
                userRepo.save(user);
                groupRepo.save(currGroup);
                memberRepo.delete(groupMember);
                return true;
            }
        }
        return false;
    }

    // Change group plan
    @PutMapping("/group/{id}/changePlan")
    public Group changePlan(@PathVariable int id, @RequestBody Map<String, Object> map){
        Group currGroup = groupRepo.findById(id).orElse(null);
        if (currGroup != null && currGroup.isModLevel((String) map.get("sessionToken"))) {
            FoodPlan plan = planRepo.findById((int) map.get("planId")).orElse(null);
            if (plan != null) {
                currGroup.setPlan(plan);
                groupRepo.save(currGroup);
            }
        }
        return currGroup;
    }

    // Owner make user mod
    @PutMapping("/group/{id}/promoteMod")
    public GroupMember promoteUser(@PathVariable int id, @RequestBody Map<String, Object> map){
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
    @PutMapping("/group/{id}/demoteMod")
    public GroupMember demoteUser(@PathVariable int id, @RequestBody Map<String, Object> map){
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
    @PutMapping("/group/{id}/makeOwner")
    public GroupMember makeOwner(@PathVariable int id, @RequestBody Map<String, Object> map){
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
    @DeleteMapping("/group/{id}")
    public Group delete(@PathVariable int id, @RequestBody String sessionToken){
        Group group = groupRepo.findById(id).orElse(null);
        if (group != null && group.isOwnerLevel(sessionToken)){
            groupRepo.delete(group);
        }
        return group;
    }

    // List
    @GetMapping("/allGroups")
    public List<Group> getAllGroups(){
        return groupRepo.findAll();
    }

    @GetMapping("/searchGroups")
    public List<Group> searchGroups(@RequestParam(defaultValue = "") String keyword){
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

}
