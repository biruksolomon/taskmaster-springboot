package com.taskmaster_springboot.repository;

import com.taskmaster_springboot.model.Teams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TeamsRepository extends JpaRepository<Teams, UUID> {
}
