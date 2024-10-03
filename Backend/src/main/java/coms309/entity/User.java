package coms309.entity;

import jakarta.persistence.*;

@Entity
@Table(name="user")

public class User {
    @Column(name="username")
    String username; //unique

    @Column(name="password")
    String password; //will have to figure out how to hash it

    @Column(name="profilepicture")
    int img;
    @Column(name="fname")
    String fname;
    @Column(name="lname")
    String lname;
    @Column(name="height") //in inches
    int height;
    @Column(name="weight") //in lbs
    int weight;

    enum Account {
        USER,
        CONTRIBUTOR,
        ADMINISTRATOR
    }
    @Column(name="accounttype")
    Account accounttype;


    public User() {}
    public User(String username, String password, String fname, String lname) {
        this.username = username;
        this.password = password;
        this.fname = fname;
        this.lname = lname;
        this.height = -1;
        this.weight = -1;
        this.accounttype = Account.USER;
    }

    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getFName() {return fname;}
    public String getLName() {return lname;}
    public int getHeight() {return height;}
    public int getWeight() {return weight;}
    public String getAccountType() {return accounttype.toString();}

}
