package com.taskmaster_springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taskmaster_springboot.model.enums.InvitationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity(name = "invitations")
public class Invitations {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Invitee email is required")
    @Email(message = "Invitee email should be valid")
    @Column(name = "invitee_email", nullable = false)
    private String inviteeEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Users inviter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    @JsonIgnore
    private Teams team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    @JsonIgnore
    private Projects project;

    @Column(name = "token", nullable = false, updatable = false)
    private UUID token;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private InvitationStatus status;

    @Column(name = "role", length = 64)
    private String role;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.token == null) {
            this.token = UUID.randomUUID();
        }
        if (this.status == null) {
            this.status = InvitationStatus.PENDING; // Default status here
        }
        this.createdAt = Instant.now();
    }
}
