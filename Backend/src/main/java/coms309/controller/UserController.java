package coms309.controller;

import coms309.entity.FoodItem;
import coms309.repository.UserRepository;
import coms309.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    UserRepository userRepo;

    // Get user from id
    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable int id) {
        return userRepo.findById(id).orElse(null);
    }

    // Update user info
    @PutMapping("/user/update/{id}")
    public User updateUser(@PathVariable int id, @RequestBody Map<String, Object> updatedUser) {
        User currUser = userRepo.findById(id).orElse(null);
        if (currUser != null) {
            if (updatedUser.containsKey("password")) {
                currUser.setPassword((String)updatedUser.get("password"));
            }
            if(updatedUser.containsKey("fname")){
                currUser.setFName((String) updatedUser.get("fname"));
            }
            if(updatedUser.containsKey("lname")){
                currUser.setFName((String) updatedUser.get("lname"));
            }
            if(updatedUser.containsKey("height")){
                currUser.setHeight((int) updatedUser.get("height"));
            }
            if(updatedUser.containsKey("weight")){
                currUser.setWeight((int) updatedUser.get("weight"));
            }
            if(updatedUser.containsKey("accounttype")){
                currUser.setAccountType((User.Account) updatedUser.get("accounttype"));
            }
            if(updatedUser.containsKey("sessiontoken")){
                currUser.setSessionToken((String) updatedUser.get("sessiontoken"));
            }
            if(updatedUser.containsKey("img")){
                currUser.setImg((String) updatedUser.get("img"));
            }
            userRepo.save(currUser);
        }
        return currUser;
    }

    // Delete  user
    @DeleteMapping("/user/{id}")
    public User delete(@PathVariable int id){
        User delUser = userRepo.findById(id).orElse(null);

        if (userRepo.existsById(id)){
            userRepo.deleteById(id);
        }

        return delUser;
    }

    // List all users
    @GetMapping("/allusers")
    public List<User> getAllFoodItems() {
        List<User> list = new ArrayList<>();
        list.addAll(userRepo.findAll());
        return list;
    }

    // Sign up
    @PostMapping("/user/signup")
    public ResponseEntity<String> signup(@RequestBody User newUser) {
        if (userRepo.findByusername(newUser.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        userRepo.save(newUser);
        return ResponseEntity.ok().body("Registered Successfully");
    }

    // Log in
    @PutMapping("/login")
    public User login(@RequestBody Map<String, Object> credentials) {
        int uid = (int) credentials.get("uid");
        String password = (String) credentials.get("password");
        if (userRepo.existsById(uid)) {
            User check = userRepo.findById(uid).get();
            if (check.getPassword().equals(password)) {
                return check;
            }
            else return null;
        }
        else {
            return null;
        }
    }

    // Logout



    // Forget Password
    @PutMapping("/password")
    public User forgotPassword(@RequestBody Map<String, Object> credentials) {
        int uid = (int)credentials.get("uid");
        String password = (String) credentials.get("password");
        if (userRepo.existsById(uid)) {
            User check = userRepo.findById(uid).get();
            check.setPassword((String)credentials.get("newPassword"));
            userRepo.save(check);
            return check;
        }
        else {
            return null;
        }
    }


    // Update user profile

}
