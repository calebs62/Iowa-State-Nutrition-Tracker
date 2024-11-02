package coms309.entity;

import jakarta.persistence.*;

@Entity
@Table(name="SystemNotificationQueue")
public class SystemNotificationQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;


}
