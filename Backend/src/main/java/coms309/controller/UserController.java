package coms309.controller;

import coms309.entity.FoodItem;
import coms309.repository.UserRepository;
import coms309.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    UserRepository userRepo;

    // Get user from id
    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable String id) {
        return userRepo.findById(id).get();
    }

    // Update user info

    // Delete  user


    // List all users
    @GetMapping("/allusers")
    public List<User> getAllFoodItems() {
        List<User> list = new ArrayList<>();
        list.addAll(userRepo.findAll());
        return list;
    }
    // Sign up
    @PostMapping("/user/signup")
    public User signup(@RequestBody User tmp){
        //if (userRepo.exists())
        return userRepo.save(tmp);
    }

    // Log in
    @GetMapping("/login")
    public User login(@RequestBody String[] credentials) {
        //credentials[0] = username
        //credentials[1] = password
        String username = credentials[0];
        String password = credentials[1];
        if (userRepo.existsById(username)) {
            User check = userRepo.findById(username).get();
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


}
