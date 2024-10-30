package coms309.controller;

import coms309.entity.Group;
import coms309.entity.GroupMember;
import coms309.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class GroupController {
    @Autowired
    GroupRepository groupRepo;

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
        if (currGroup != null  && currGroup.isModLevel((int) newGroup.get("userId"))){
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
        if (currGroup != null && currGroup.isModLevel((int) newMembers.get("userId"))){
            currGroup.addMember((GroupMember) newMembers.get("member"));
        }
        return currGroup;
    }

    @PutMapping("/group/{id}/removeMember")
    public Group removeMember(@PathVariable int id, @RequestBody Map<String, Object> newMembers){
        Group currGroup = groupRepo.findById(id).orElse(null);
        if (currGroup != null && currGroup.isModLevel((int) newMembers.get("userId"))){
            currGroup.removeMember((GroupMember) newMembers.get("member"));
        }
        return currGroup;
    }

    // Delete
    @DeleteMapping("/group/{id}")
    public Group delete(@PathVariable int id){
        Group group = groupRepo.findById(id).orElse(null);
        if (groupRepo.existsById(id)){
            groupRepo.deleteById(id);
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
