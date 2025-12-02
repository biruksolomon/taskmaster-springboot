package com.taskmaster_springboot.repository;

import com.taskmaster_springboot.model.TeamMembers;
import com.taskmaster_springboot.model.TeamMembersId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamMembersRepository extends JpaRepository<TeamMembers, TeamMembersId> {
    @Query("SELECT tm FROM team_members tm WHERE tm.user.userId = :userid")
    List<TeamMembers> findByUserId(@Param("userid") UUID userid);
}

