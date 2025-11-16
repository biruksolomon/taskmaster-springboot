package com.taskmaster_springboot.repository;

import com.taskmaster_springboot.model.Invitations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InvitationRepository extends JpaRepository<Invitations, UUID> {
}
