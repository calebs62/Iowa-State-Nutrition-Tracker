package coms309.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import coms309.entity.SystemNotificationQueue;

@Repository
public interface SystemNotificationQueueRepository extends JpaRepository<SystemNotificationQueue, Integer> {
}
