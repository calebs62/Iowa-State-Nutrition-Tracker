package coms309.repository;

import coms309.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByusername(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.membered WHERE u.uid = :id")
    User findByIdWithMemberships(@Param("id") Integer id);
}
