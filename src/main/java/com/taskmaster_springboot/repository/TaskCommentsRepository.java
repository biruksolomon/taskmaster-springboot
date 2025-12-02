package com.taskmaster_springboot.repository;

import com.taskmaster_springboot.model.TaskComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskCommentsRepository extends JpaRepository<TaskComments, UUID> {
    List<TaskComments> findByTaskId(UUID taskId);
}
