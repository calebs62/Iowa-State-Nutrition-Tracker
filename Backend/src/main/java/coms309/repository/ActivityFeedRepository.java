package coms309.repository;

import coms309.entity.ActivityFeed;
import coms309.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ActivityFeedRepository extends JpaRepository<ActivityFeed, Integer> {

    @Query("SELECT DISTINCT a FROM ActivityFeed a LEFT JOIN FETCH a.images " +
            "WHERE a.group.id IN :groupIds AND a.timestamp > :timestamp " +
            "ORDER BY a.timestamp DESC")
    List<ActivityFeed> findRecentActivitiesForGroups(@Param("groupIds") List<Integer> groupIds,
                                                     @Param("timestamp") Timestamp timestamp);

    @Query("SELECT DISTINCT a FROM ActivityFeed a LEFT JOIN FETCH a.images " +
            "WHERE a.group.id IN :groupIds AND a.timestamp > :timestamp " +
            "AND a.timestamp < :currentTime " +
            "ORDER BY a.timestamp DESC")
    List<ActivityFeed> findRecentActivitiesExcludingLatest(
            @Param("groupIds") List<Integer> groupIds,
            @Param("timestamp") Timestamp timestamp,
            @Param("currentTime") Timestamp currentTime);

    List<ActivityFeed> findByGroup(Group group);
}