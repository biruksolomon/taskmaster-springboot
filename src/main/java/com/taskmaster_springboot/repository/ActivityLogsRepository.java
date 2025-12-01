package com.taskmaster_springboot.repository;

import com.taskmaster_springboot.model.ActivityLogs;
import com.taskmaster_springboot.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityLogsRepository extends JpaRepository<ActivityLogs, Long> {

    @Query("select AL from activity_logs AL where AL.entityId=:entityid ")
    List<ActivityLogs> findByEntityId(@Param("entityid") UUID entityId);

    @Query("select AL from activity_logs  AL where AL.actor=:user")
    List<ActivityLogs> findByActor(@Param("user") Users user);
}
