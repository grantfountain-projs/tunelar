package com.tunelar.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tunelar.backend.model.Role;

/**
 * Repository interface for Roles.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * finds a role by name
     *
     * @param name the name of the role
     *            
     * @return the role with that name
     */
    Role findByName (String name);
}