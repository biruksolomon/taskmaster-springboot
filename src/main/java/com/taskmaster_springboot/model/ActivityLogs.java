package com.taskmaster_springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity(name = "activity_logs")
@Builder
@AllArgsConstructor
public class ActivityLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activity_logs_seq")
    @SequenceGenerator(
            name = "activity_logs_seq",
            sequenceName = "activity_logs_seq",
            allocationSize = 1
    )
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Users actor;

    @NotBlank(message = "Action is required")
    @Column(name = "action", nullable = false, length = 150)
    private String action;

    @Column(name = "entity_type", length = 64)
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Lob
    @Column(name = "data", columnDefinition = "JSONB")
    private String data;

    @Column(name = "ip", length = 64)
    private String ip;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public ActivityLogs() {

    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
