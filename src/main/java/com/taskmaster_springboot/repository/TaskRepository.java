package com.taskmaster_springboot.repository;

import com.taskmaster_springboot.model.Projects;
import com.taskmaster_springboot.model.Tasks;
import com.taskmaster_springboot.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Tasks, UUID> {
    List<Tasks> findByProject(Projects project);
    List<Tasks> findByAssignee(Users assignee);
    long countByProject(Projects project);
}
