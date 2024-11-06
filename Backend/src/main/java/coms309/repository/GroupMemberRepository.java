package coms309.repository;

import coms309.entity.GroupMember;
import coms309.entity.GroupMemberKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberKey> {
}