package com.taskmaster_springboot.repository;

import com.taskmaster_springboot.model.ActivityLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogsRepository extends JpaRepository<ActivityLogs, Long> {
}
