package com.taskmaster_springboot.model;

import com.taskmaster_springboot.model.enums.RoleName;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "roles")
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private RoleName name;

    private String description;

}
