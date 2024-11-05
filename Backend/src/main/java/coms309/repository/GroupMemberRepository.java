package coms309.repository;

import coms309.entity.GroupMember;
import coms309.entity.GroupMemberKey;
import coms309.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> {
    GroupMember findByuser(User user);
}