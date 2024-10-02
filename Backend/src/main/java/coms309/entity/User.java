package coms309.entity;

public class User {
    String username; //unique
    String password; //will have to figure out how to hash it

    public User() {}
    public User(String newUsername, String newPassword) {
        this.username = newUsername;
        this.password = newPassword;
    }


}
