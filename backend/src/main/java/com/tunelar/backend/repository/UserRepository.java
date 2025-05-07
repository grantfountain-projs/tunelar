package com.tunelar.backend.repository;

import com.tunelar.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    /**
     * Returns the user object associated with either the username or email
     * @param username user's username
     * @param email user's email
     * @return User object or exception on error
     */
    Optional<User> findByUsernameOrEmail(String username, String email);
}