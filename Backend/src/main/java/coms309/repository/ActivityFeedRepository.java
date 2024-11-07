package coms309.repository;

import coms309.entity.ActivityFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ActivityFeedRepository extends JpaRepository<ActivityFeed, Integer> {

    @Query("SELECT a FROM ActivityFeed a WHERE a.group.id IN :groupIds AND a.timestamp > :timestamp ORDER BY a.timestamp DESC")
    List<ActivityFeed> findRecentActivitiesForGroups(@Param("groupIds") List<Integer> groupIds,
                                                     @Param("timestamp") Timestamp timestamp);
}