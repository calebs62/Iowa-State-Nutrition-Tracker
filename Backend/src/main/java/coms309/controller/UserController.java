package coms309.controller;

import coms309.repository.UserRepository;
import coms309.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    UserRepository userRepo;

    // Create user

    // Get user from id

    // Update user info

    // Delete  user

    // List all users

    // Sign up
    @PostMapping("/user/signup")
    public User signup(@RequestBody User tmp){
        //if (userRepo.exists())
        return userRepo.save(tmp);
    }

    // Log in

    // Logout

    // Forget Password
}
