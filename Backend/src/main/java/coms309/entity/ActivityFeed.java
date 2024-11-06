package coms309.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "activity_feed")
public class ActivityFeed {

    public enum ActivityType {
        FOOD_EATEN,
        GROUP_UPDATE,
        ACHIEVEMENT,
        GOAL_UPDATE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ActivityType type;

    @Column(nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "additional_data")
    private String additionalData;


    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;


}
