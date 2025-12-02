package com.taskmaster_springboot.repository;

import com.taskmaster_springboot.model.Projects;
import com.taskmaster_springboot.model.Teams;
import com.taskmaster_springboot.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Projects, UUID> {
    List<Projects> findByTeam(Teams team);
    List<Projects> findByOwner(Users owner);
}
