package com.tunelar.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    // You can add a description field if needed
    private String description;

    /**
     * Returns the role name for string representation
     * Overridden to make tests work correctly
     * 
     * @return the role name
     */
    @Override
    public String toString() {
        return name;
    }
}