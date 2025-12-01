package com.taskmaster_springboot.service;

import com.taskmaster_springboot.dto.request.TaskCommentCreateRequestDTO;
import com.taskmaster_springboot.dto.response.TaskCommentResponseDTO;

import java.util.List;
import java.util.UUID;

public interface TaskCommentService {
    TaskCommentResponseDTO addComment(TaskCommentCreateRequestDTO request, String email);
    TaskCommentResponseDTO getCommentById(UUID commentId);
    List<TaskCommentResponseDTO> getTaskComments(UUID taskId);
    TaskCommentResponseDTO updateComment(UUID commentId, String content, String email);
    void deleteComment(UUID commentId, String email);
}
