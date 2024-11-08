package coms309.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "earned")
public class Earned {
    @EmbeddedId
    @Column(name = "earnedId")
    private EarnedKey id;

    @
    @Column(name = "earnDate")
    private Date earnDate = new Date();
}
