package com.taskmaster_springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(name = "teams")
public class Teams {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Team name is required")
    @Size(min = 2, max = 200, message = "Team name must be between 2 and 200 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 200, message = "Slug must be less than 200 characters")
    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    @JsonIgnore
    private Users createdBy;

    @Lob
    @Column(name = "metadata", columnDefinition = "JSONB")
    private String metadata; // Can store JSON structure as string

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant updatedAt;

    @Column(name = "archived", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean archived = false;

    @ManyToMany(mappedBy = "team")
    @JsonIgnore
    private Set<Users> members = new HashSet<>();


    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
