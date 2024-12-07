package coms309.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="user")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="UID")
    @JsonView(value = {Views.Public.class})
    private int uid;

    @Column(name="username", unique = true)
    @JsonView(value = {Views.Public.class})
    private String username; //unique

    @Column(name="password")
    @JsonView(value = {Views.User.class})
    private String password; //will have to figure out how to hash it

    @Column(name="profilepicture",
            columnDefinition="LONGTEXT")
    @JsonView(value = {Views.User.class})
    private String img;
    @Column(name="fname")
    @JsonView(value = {Views.Public.class})
    private String fname;
    @Column(name="lname")
    @JsonView(value = {Views.Public.class})
    private String lname;
    @Column(name="height") //in inches
    @JsonView(value = {Views.Public.class})
    private int height = -1;
    @Column(name="weight") //in lbs
    @JsonView(value = {Views.Public.class})
    private int weight = -1;

    @OneToMany(mappedBy = "user")
    @JsonView(Views.User.class)
    private Set<GroupMember> membered = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @JsonView(Views.User.class)
    private Set<Earned> earned = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @JsonView(Views.User.class)
    private Set<FoodEaten> eaten = new HashSet<>();

    public enum Account {
        USER,
        CONTRIBUTOR,
        ADMINISTRATOR
    }
    @Column(name="accounttype")
    @JsonView(value = {Views.User.class, Views.GroupMember.class})
    private Account accounttype = Account.USER;

    @Column(name="sessionToken")
    @JsonView(value = {Views.User.class, Views.GroupMember.class})
    private String sessionToken = "0:0:0";
    @Column(name="lastLogin")
    @JsonView(value = {Views.User.class})
    private Timestamp lastLogin = new Timestamp(System.currentTimeMillis());

    @Column(name="bodyType")
    private String bodyType;

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
    public Timestamp getLastLogin() {return lastLogin;}
    public String getBodyType() {
        if (bodyType == null) {
            bodyType = "";
        }
        return bodyType;}

    public Set<GroupMember> getMembered() {
        return membered;
    }

    public void setId(int uid) {this.uid = uid;}
    public void setPassword(String password) {this.password = password;}
    public void setFName(String fname) {this.fname = fname;}
    public void setLName(String lname) {this.lname = lname;}
    public void setHeight(int height) {this.height = height;}
    public void setWeight(int weight) {this.weight = weight;}
    public void setAccountType(Account accounttype) {this.accounttype = accounttype;}
    public void setSessionToken(String sessionToken) {this.sessionToken = sessionToken;}
    public void setImg(String img){this.img = img;}

    public void setLoginNow() {
        lastLogin = new Timestamp(System.currentTimeMillis());
    }

    public void setBodyType(String s) {
        bodyType = s;}

    public void setMembered(Set<GroupMember> membered) {
        this.membered = membered;
    }

    public void addMembered(GroupMember membered){
        this.membered.add(membered);
    }

    public void removeMembered(GroupMember membered){
        this.membered.remove(membered);
    }

    public void updateSessionToken(){
        String[] tmp = sessionToken.split(":", 3);
        sessionToken = tmp[0] + ":" + accounttype.ordinal() + ":" + uid;
    }
    public void loginSession(){
        String[] tmp = sessionToken.split(":", 3);
        tmp[0] = "1";
        sessionToken = tmp[0] + ":" + accounttype.ordinal() + ":" + uid;
        setLoginNow();
    }
    public void logoutSession(){
        String[] tmp = sessionToken.split(":", 3);
        tmp[0] = "0";
        sessionToken = tmp[0] + ":" + accounttype.ordinal() + ":" + uid;
    }

    public Set<Earned> getEarned() {
        return earned;
    }

    public void setEarned(Set<Earned> earned) {
        this.earned = earned;
    }

    public Set<FoodEaten> getEaten() {
        return eaten;
    }

    public void setEaten(Set<FoodEaten> eaten) {
        this.eaten = eaten;
    }

    public void addEaten(FoodEaten eaten){
        this.eaten.add(eaten);
    }

    public void removeEaten(FoodEaten eaten){
        this.eaten.remove(eaten);
    }

}
