package coms309.repository;

import coms309.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

public interface ActivityFeedRepository extends JpaRepository<ActivityFeed, Integer> {
    List<ActivityFeed> findByGroup(Group g);

}