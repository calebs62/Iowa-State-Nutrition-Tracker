package coms309.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name="user")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="UID")
    private int uid;

    @Column(name="username", unique = true)
    private String username; //unique

    @Column(name="password")
    private String password; //will have to figure out how to hash it

    @Column(name="profilepicture",
            columnDefinition="LONGTEXT")
    private String img;
    @Column(name="fname")
    private String fname;
    @Column(name="lname")
    private String lname;
    @Column(name="height") //in inches
    private int height = -1;
    @Column(name="weight") //in lbs
    private int weight = -1;

    //TODO - do we want public?
    public enum Account {
        USER,
        CONTRIBUTOR,
        ADMINISTRATOR
    }
    @Column(name="accounttype")
    private Account accounttype = Account.USER;

    @Column(name="sessionToken")
    private String sessionToken = "0:0:0";

    @OneToMany
    private Set<FoodEaten> eaten;

    public User() {}
    public User(String username, String password, String fname, String lname) {
        this.username = username;
        this.password = password;
        this.fname = fname;
        this.lname = lname;
    }

    public int getUid(){return uid;}
    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getFName() {return fname;}
    public String getLName() {return lname;}
    public int getHeight() {return height;}
    public int getWeight() {return weight;}
    public String getAccountType() {return accounttype.toString();}
    public String getSessionToken() {return sessionToken;}
    public String getImg(){return img;}

    public void setId(int uid) {this.uid = uid;}
    public void setPassword(String password) {this.password = password;}
    public void setFName(String fname) {this.fname = fname;}
    public void setLName(String lname) {this.lname = lname;}
    public void setHeight(int height) {this.height = height;}
    public void setWeight(int weight) {this.weight = weight;}
    public void setAccountType(Account accounttype) {this.accounttype = accounttype;}
    public void setSessionToken(String sessionToken) {this.sessionToken = sessionToken;}
    public void setImg(String img){this.img = img;}

    public void updateSessionToken(){
        String[] tmp = sessionToken.split(":", 3);
        sessionToken = tmp[0] + ":" + accounttype.ordinal() + ":" + uid;
    }
    public void loginSession(){
        String[] tmp = sessionToken.split(":", 3);
        tmp[0] = "1";
        sessionToken = tmp[0] + ":" + accounttype.ordinal() + ":" + uid;
    }
    public void logoutSession(){
        String[] tmp = sessionToken.split(":", 3);
        tmp[0] = "0";
        sessionToken = tmp[0] + ":" + accounttype.ordinal() + ":" + uid;
    }
}
