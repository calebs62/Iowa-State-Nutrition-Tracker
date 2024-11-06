package coms309.repository;

import coms309.entity.ActivityFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ActivityFeedRepository extends JpaRepository<ActivityFeed, Integer> {

}