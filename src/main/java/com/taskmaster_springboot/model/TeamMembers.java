package com.taskmaster_springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taskmaster_springboot.model.enums.TeamRole;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity(name = "team_members")
public class TeamMembers {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Teams team;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'member'")
    private TeamRole role = TeamRole.MEMBER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by", referencedColumnName = "id")
    @JsonIgnore
    private Users invitedBy;

    @Column(name = "joined_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant joinedAt;

    @PrePersist
    protected void onJoin() {
        this.joinedAt = Instant.now();
    }
}
