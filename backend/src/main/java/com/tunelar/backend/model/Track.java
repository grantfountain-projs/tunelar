package com.tunelar.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "tracks")
public class Sample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    private String description;
    
    @Column(nullable = false)
    private String fileUrl;
    
    private String waveformUrl;
    
    @Column(nullable = false)
    private Long fileSize;
    
    @Column(nullable = false)
    private Integer duration;
    
    @Column(nullable = false)
    private String fileType;
    
    private Integer bpm;
    
    private String key;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToMany
    @JoinTable(
        name = "track_tags",
        joinColumns = @JoinColumn(name = "track_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Transient
    public boolean isSample() {
        return this.tags.stream().anyMatch(tag -> tag.getName().equalsIgnoreCase("sample"));
    }
    
    @Transient
    public boolean isLoop() {
        return this.tags.stream().anyMatch(tag -> tag.getName().equalsIgnoreCase("loop"));
    }
    
    @Transient
    public boolean isDrum() {
        return this.tags.stream().anyMatch(tag -> tag.getName().equalsIgnoreCase("drum"));
    }
    
    @Transient
    public boolean isBeat() {
        return this.tags.stream().anyMatch(tag -> tag.getName().equalsIgnoreCase("beat"));
    }
}