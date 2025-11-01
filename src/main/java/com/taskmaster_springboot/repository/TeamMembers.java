package com.taskmaster_springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMembers extends JpaRepository<TeamMembers, Long> {
}
