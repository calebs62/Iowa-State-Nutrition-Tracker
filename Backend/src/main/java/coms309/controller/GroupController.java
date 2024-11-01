package coms309.controller;

import coms309.entity.FoodPlan;
import coms309.entity.Group;
import coms309.entity.GroupMember;
import coms309.entity.User;
import coms309.repository.GroupMemberRepository;
import coms309.repository.GroupRepository;
import coms309.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class GroupController {
    @Autowired
    GroupRepository groupRepo;
    @Autowired
    UserRepository userRepo;
    @Autowired
    GroupMemberRepository memberRepo;

    // Create
    @PostMapping("/group")
    public Group createGroup(@RequestBody Group group){
        return groupRepo.save(group);
    }

    // Read
    @GetMapping("/group/{id}")
    public Group getGroupById(@PathVariable int id){
        return groupRepo.findById(id).orElse(null);
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

    @PutMapping("/group/{id}/addMember")
    public Group addMember(@PathVariable int id, @RequestBody Map<String, Object> newMembers){
        Group currGroup = groupRepo.findById(id).orElse(null);
        if (currGroup != null && currGroup.isModLevel((String) newMembers.get("sessionToken"))){
            currGroup.addMember((GroupMember) newMembers.get("member"));
        }
        return currGroup;
    }

    @PutMapping("/group/{id}/removeMember")
    public Group removeMember(@PathVariable int id, @RequestBody Map<String, Object> newMembers){
        Group currGroup = groupRepo.findById(id).orElse(null);
        if (currGroup != null && currGroup.isModLevel((String) newMembers.get("sessionToken"))){
            currGroup.removeMember((GroupMember) newMembers.get("member"));
        }
        return currGroup;
    }

    @PutMapping("/group/{id}/join")
    public Boolean memberJoin(@PathVariable int id, @RequestBody String sessionToken){
        Group currGroup = groupRepo.findById(id).orElse(null);
        String[] array = sessionToken.split(":");
        int uid = Integer.parseInt(array[2]);
        User currUser = userRepo.findById(uid).orElse(null);
        if (currGroup != null && currUser != null) {
            GroupMember groupMember = new GroupMember(currGroup, currUser);
            memberRepo.save(groupMember);
            return currGroup.addMember(groupMember);
        }
        return false;
    }

    //TODO - fix, shouldn't create new groupMember should find current
    @PutMapping("/group/{id}/leave")
    public Boolean memberLeave(@PathVariable int id, @RequestBody String sessionToken){
        Group currGroup = groupRepo.findById(id).orElse(null);
        String[] array = sessionToken.split(":");
        int uid = Integer.parseInt(array[2]);
        User currUser = userRepo.findById(uid).orElse(null);
        if (currGroup != null && currUser != null) {
            Set<GroupMember> groupMembers = currGroup.getMembers();
            //TODO
            GroupMember groupMember = new GroupMember(currGroup, currUser);
            memberRepo.save(groupMember);
            return currGroup.removeMember(groupMember);
        }
        return false;
    }

    // Change group plan
    @PutMapping("/group/{id}/changePlan")
    public Group changePlan(@PathVariable int id, @RequestBody Map<String, Object> map){
        Group currGroup = groupRepo.findById(id).orElse(null);
        if (currGroup != null && currGroup.isModLevel((String) map.get("sessionToken"))) {
            currGroup.setPlan((FoodPlan) map.get("newPlan"));
        }
        return currGroup;
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
