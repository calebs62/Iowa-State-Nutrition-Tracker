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

    @Column(name = "goal")
    @JsonView(value = {Views.Public.class})
    private Integer goal;

    @Column(name = "icon",
            columnDefinition = "LONGTEXT")
    @JsonView(value = {Views.Achievement.class})
    private String icon;

    @OneToMany(mappedBy = "achievement")
    @JsonView(value = {Views.Achievement.class})
    private Set<Earned> earnedBy = new HashSet<>();

    public Achievement() {}

    public Achievement(String name, String description, String icon, int goal) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.goal = goal;
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

    public Set<Earned> getEarnedBy() {
        return earnedBy;
    }

    public void setEarnedBy(Set<Earned> earnedBy) {
        this.earnedBy = earnedBy;
    }

    public void addEarnedBy(Earned earnedBy){
        this.earnedBy.add(earnedBy);
    }

    public void removeEarnedBy(Earned earnedBy){
        this.earnedBy.remove(earnedBy);
    }

    public Integer getGoal() {
        return goal;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }

    public Earned findUser(int uid){
        for (Earned em : earnedBy){
            User user = em.getUser();
            if (user.getUid() == uid){
                return em;
            }
        }
        return null;
    }
}
