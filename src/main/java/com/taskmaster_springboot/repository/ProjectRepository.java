package com.taskmaster_springboot.repository;

import com.taskmaster_springboot.model.Projects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Projects, UUID> {
}
