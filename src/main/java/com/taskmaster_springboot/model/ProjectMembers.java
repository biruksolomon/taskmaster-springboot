package com.taskmaster_springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity(name = "project_members")
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMembers {

    @EmbeddedId
    private ProjectMembersId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "project_id",
            insertable = false,
            updatable = false
    )
    @JsonIgnore
    private Projects project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            insertable = false,
            updatable = false
    )
    @JsonIgnore
    private Users user;

    @Column(name = "role", nullable = false, length = 64)
    private String role = "contributor"; // Default assigned in Java

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    @PrePersist
    public void prePersist() {
        if (this.role == null) {
            this.role = "contributor";
        }
        if (this.joinedAt == null) {
            this.joinedAt = Instant.now();
        }
    }
}
