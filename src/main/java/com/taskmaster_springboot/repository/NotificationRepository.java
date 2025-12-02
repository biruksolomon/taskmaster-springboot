package com.taskmaster_springboot.repository;

import com.taskmaster_springboot.model.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, UUID> {
    @Query("select n from notifications n where n.user.userId=:userid")
    List<Notifications> findByUserId(@Param("userid") UUID userId);
}
