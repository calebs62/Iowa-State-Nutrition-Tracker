package coms309.entity;

import jakarta.persistence.*;
import org.antlr.v4.runtime.Token;

@Entity
@Table(name="user")

public class User {
    @Id
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

    //TODO - do we want public?
    public enum Account {
        USER,
        CONTRIBUTOR,
        ADMINISTRATOR
    }
    @Column(name="accounttype")
    Account accounttype;

    @Column(name="sessionToken")
    String sessionToken;

    public User() {}
    public User(String username, String password, String fname, String lname) {
        this.username = username;
        this.password = password;
        this.fname = fname;
        this.lname = lname;
        this.height = -1;
        this.weight = -1;
        this.accounttype = Account.USER;
        this.sessionToken = "**";
    }

    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getFName() {return fname;}
    public String getLName() {return lname;}
    public int getHeight() {return height;}
    public int getWeight() {return weight;}
    public String getAccountType() {return accounttype.toString();}
    public String getSessionToken() {return sessionToken;}

    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    public void setFName(String fname) {this.fname = fname;}
    public void setLName(String lname) {this.lname = lname;}
    public void setHeight(int height) {this.height = height;}
    public void setWeight(int weight) {this.weight = weight;}
    public void setAccountType(Account accounttype) {this.accounttype = accounttype;}
    public void setSessionToken(String sessionToken) {this.sessionToken = sessionToken;}

}
