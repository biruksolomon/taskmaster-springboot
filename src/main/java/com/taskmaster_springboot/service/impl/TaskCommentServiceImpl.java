package com.taskmaster_springboot.service.impl;

import com.taskmaster_springboot.dto.request.TaskCommentCreateRequestDTO;
import com.taskmaster_springboot.dto.response.TaskCommentResponseDTO;
import com.taskmaster_springboot.exceptions.AuthenticationException;
import com.taskmaster_springboot.exceptions.ResourceNotFoundException;
import com.taskmaster_springboot.model.TaskComments;
import com.taskmaster_springboot.model.Tasks;
import com.taskmaster_springboot.model.Users;
import com.taskmaster_springboot.repository.TaskCommentsRepository;
import com.taskmaster_springboot.repository.TaskRepository;
import com.taskmaster_springboot.repository.UsersRepository;
import com.taskmaster_springboot.service.TaskCommentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class TaskCommentServiceImpl implements TaskCommentService {

    private final TaskCommentsRepository taskCommentsRepository;
    private final TaskRepository taskRepository;
    private final UsersRepository usersRepository;

    @Override
    public TaskCommentResponseDTO addComment(TaskCommentCreateRequestDTO request, String email) {
        log.info("Adding comment to task");

        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            throw new AuthenticationException("User not found");
        }

        Tasks task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        TaskComments comment = TaskComments.builder()
                .task(task)
                .author(user)
                .content(request.getContent())
                .build();

        if (request.getParentCommentId() != null) {
            TaskComments parentComment = taskCommentsRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
            comment.setParentComment(parentComment);
        }

        TaskComments savedComment = taskCommentsRepository.save(comment);
        log.info("Comment added successfully: {}", savedComment.getId());

        return mapToDTO(savedComment);
    }

    @Override
    public TaskCommentResponseDTO getCommentById(UUID commentId) {
        TaskComments comment = taskCommentsRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        return mapToDTO(comment);
    }

    @Override
    public List<TaskCommentResponseDTO> getTaskComments(UUID taskId) {
        return taskCommentsRepository.findByTaskId(taskId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskCommentResponseDTO updateComment(UUID commentId, String content, String email) {
        TaskComments comment = taskCommentsRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        Users user = usersRepository.findByEmail(email);
        if (user == null || !comment.getAuthor().getUserId().equals(user.getUserId())) {
            throw new AuthenticationException("Unauthorized to update this comment");
        }

        comment.setContent(content);
        comment.setEditedAt(java.time.Instant.now());

        TaskComments updatedComment = taskCommentsRepository.save(comment);
        return mapToDTO(updatedComment);
    }

    @Override
    public void deleteComment(UUID commentId, String email) {
        TaskComments comment = taskCommentsRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        Users user = usersRepository.findByEmail(email);
        if (user == null || !comment.getAuthor().getUserId().equals(user.getUserId())) {
            throw new AuthenticationException("Unauthorized to delete this comment");
        }

        taskCommentsRepository.delete(comment);
        log.info("Comment deleted: {}", commentId);
    }

    private TaskCommentResponseDTO mapToDTO(TaskComments comment) {
        return TaskCommentResponseDTO.builder()
                .id(comment.getId())
                .taskId(comment.getTask().getId())
                .authorId(comment.getAuthor().getUserId())
                .authorName(comment.getAuthor().getFirstName() + " " + comment.getAuthor().getLastName())
                .content(comment.getContent())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .createdAt(comment.getCreatedAt())
                .editedAt(comment.getEditedAt())
                .build();
    }
}
