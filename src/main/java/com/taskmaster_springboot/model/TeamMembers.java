package com.taskmaster_springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taskmaster_springboot.model.enums.TeamRole;
import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity(name = "team_members")
@NoArgsConstructor
public class TeamMembers {

    @EmbeddedId
    private TeamMembersId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "team_id",
            referencedColumnName = "id",
            nullable = false,
            insertable = false,
            updatable = false
    )
    @JsonIgnore
    private Teams team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            insertable = false,
            updatable = false
    )
    @JsonIgnore
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private TeamRole role = TeamRole.MEMBER; // Default role

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by", referencedColumnName = "id")
    @JsonIgnore
    private Users invitedBy;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    @PrePersist
    protected void onJoin() {
        if (this.joinedAt == null) {
            this.joinedAt = Instant.now();
        }
    }
}
