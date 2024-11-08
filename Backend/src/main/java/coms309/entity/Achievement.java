package coms309.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "achievement")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achivement_id")
    @JsonView(value = {Views.Public.class})
    private int id;

    @Column(name = "name")
    @JsonView(value = {Views.Public.class})
    private String name;

    @Column(name = "description")
    @JsonView(value = {Views.Achievement.class})
    private String description;

    @Column(name = "icon",
            columnDefinition = "LONGTEXT")
    @JsonView(value = {Views.Achievement.class})
    private String icon;

    @JsonView(value = {Views.Achievement.class})
    private Set<User> users = new HashSet<>();

    public Achievement() {}

    public Achievement(String name, String description, String icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void addUsers(User user){
        this.users.add(user);
    }

    public void removeUsers(User user){
        this.users.remove(user);
    }
}
