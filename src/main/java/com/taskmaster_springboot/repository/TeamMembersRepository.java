package com.taskmaster_springboot.repository;

import com.taskmaster_springboot.model.TeamMembers;
import com.taskmaster_springboot.model.TeamMembersId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMembersRepository extends JpaRepository<TeamMembers, TeamMembersId> {
}
