package coms309.controller;

import com.fasterxml.jackson.annotation.JsonView;
import coms309.entity.Views;
import coms309.repository.UserRepository;
import coms309.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

@RestController
public class UserController {
    @Autowired
    UserRepository userRepo;

    // Get user from id
    @GetMapping("/user/{id}")
    @JsonView(value = {Views.User.class})
    public User getUserById(@PathVariable int id) {
        return userRepo.findById(id).orElse(null);
    }

    // Update user info
    @PutMapping("/user/update/{id}")
    @JsonView(value = {Views.User.class})
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
                currUser.setLName((String) updatedUser.get("lname"));
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
    @JsonView(value = {Views.User.class})
    public User delete(@PathVariable int id){
        User delUser = userRepo.findById(id).orElse(null);

        if (userRepo.existsById(id)){
            userRepo.deleteById(id);
        }

        return delUser;
    }

    // List all users
    @GetMapping("/allusers")
    @JsonView(value = {Views.User.class})
    public List<User> getAllFoodItems() {
        List<User> list = new ArrayList<>();
        list.addAll(userRepo.findAll());
        return list;
    }

    // Sign up
    @PostMapping("/user/signup")
    @JsonView(value = {Views.User.class})
    public ResponseEntity<String> signup(@RequestBody User newUser) {
        if (userRepo.findByusername(newUser.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        newUser.updateSessionToken();
        userRepo.save(newUser);
        return ResponseEntity.ok().body("Registered Successfully");
    }

    // Log in
    @PutMapping("/login")
    @JsonView(value = {Views.User.class})
    public User login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        if (userRepo.findByusername(username) != null) {
            User check = userRepo.findByusername(username);
            if (check.getPassword().equals(password)) {
                AchievementController.consecutiveLogins(check, check.getLastLogin() != null ? check.getLastLogin() : new Timestamp(System.currentTimeMillis()));
                check.loginSession();
                userRepo.save(check);
                return check;
            }
        }
         return null;
    }

    // Logout
    @PutMapping("/logout")
    @JsonView(value = {Views.User.class})
    public String logout(@RequestBody String sessionToken){
        String[] array = sessionToken.split(":",3);
        int uid = Integer.parseInt(array[2].trim());
        User currUser = userRepo.findById(uid).orElse(null);
        if (currUser != null){
            currUser.logoutSession();
            userRepo.save(currUser);
            return "Logout successful";
        }
        return "Logout not successful";
    }


    // Forget Password
    @PutMapping("/password")
    @JsonView(value = {Views.User.class})
    public User forgotPassword(@RequestBody Map<String, Object> credentials) {
        String username = (String)credentials.get("username");
        String password = (String) credentials.get("password");
        if (userRepo.findByusername(username) != null) {
            User check = userRepo.findByusername(username);
            check.setPassword((String)credentials.get("newPassword"));
            userRepo.save(check);
            return check;
        }
        return null;
    }

    // Set user to Contributor
    @PutMapping("/give/{uid}/Contributor")
    @JsonView(value = {Views.User.class})
    public ResponseEntity<String> makeContributor(@PathVariable int uid, @RequestBody String sessionToken){
        User currUser = userRepo.findById(uid).orElse(null);
        String[] array = sessionToken.split(":");
        if (currUser == null) {
            return ResponseEntity.notFound().build(); //User doesn't exist
        }
        if (Integer.parseInt(array[1]) == 0 || Integer.parseInt(array[1]) > 2){
            return ResponseEntity.badRequest().body("User not authorized");
        }
        currUser.setAccountType(User.Account.CONTRIBUTOR);
        userRepo.save(currUser);
        return ResponseEntity.ok().body("User now a Contributor");
    }

    // Set user to Admin
    @PutMapping("/give/{uid}/Administrator")
    @JsonView(value = {Views.User.class})
    public ResponseEntity<String> makeAdmin(@PathVariable int uid, @RequestBody String sessionToken){
        User currUser = userRepo.findById(uid).orElse(null);
        String[] array = sessionToken.split(":");
        if (currUser == null) {
            return ResponseEntity.notFound().build(); //User doesn't exist
        }
        if (Integer.parseInt(array[1]) != 2){
            return ResponseEntity.badRequest().body("User not authorized");
        }
        currUser.setAccountType(User.Account.ADMINISTRATOR);
        userRepo.save(currUser);
        return ResponseEntity.ok().body("User now a Administrator");
    }

    @GetMapping("/{uid}/time")
    @JsonView(value = {Views.User.class})
    public Timestamp getLastLogin(@PathVariable int id) {
        return userRepo.findById(id).orElse(null).getLastLogin();
    }

    @PutMapping("/{uid}/time")
    @JsonView(value = {Views.User.class})
    public Timestamp setLastLogin(@PathVariable int id) {
        User temp = userRepo.findById(id).orElse(null);
        temp.setLoginNow();
        userRepo.save(temp);
        return temp.getLastLogin();
    }

    @GetMapping("/{uid}/sessionToken")
    @JsonView(value = {Views.User.class})
    public String getSessionToken(@PathVariable int uid){
        User u = userRepo.findById(uid).orElse(null);
        if (u == null) {
            return null;
        }
        return u.getSessionToken();
    }

}
