package com.taskmaster_springboot.repository;

import com.taskmaster_springboot.model.ProjectMembers;
import com.taskmaster_springboot.model.ProjectMembersId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMembers, ProjectMembersId> {
}
