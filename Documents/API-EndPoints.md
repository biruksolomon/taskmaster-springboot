# TaskMaster API Endpoints Documentation

## Overview
This document contains all API endpoints available in the TaskMaster Spring Boot backend application. All endpoints require authentication via JWT token except for auth endpoints (register, login, verify, forgot-password, reset-password).

---

# Authentication & User Identity API Endpoints

| Method | Endpoint                          | Description                                                                                              | Roles | Status Code |
|--------|-----------------------------------|----------------------------------------------------------------------------------------------------------|-------|-------------|
| POST   | `/api/v1/auth/register`           | User registration with email and password. User role is auto-assigned as USER. Verification email sent.   | Public | 201 |
| POST   | `/api/v1/auth/login`              | User login with email and password. Returns JWT access token and refresh token.                          | Public | 200 |
| GET    | `/api/v1/auth/verify?email=...&token=...`| Verify user email using the verification token sent to the provided email address. | Public | 200 |
| POST   | `/api/v1/auth/forgot-password`    | Request password reset email. Requires email verification before requesting a password reset.             | Public | 200 |
| POST   | `/api/v1/auth/reset-password`     | Reset user password using the token received in the password reset email.                                | Public | 200 |

---

# User Management API Endpoints (Admin Only)

| Method | Endpoint                          | Description                                                           | Roles | Status Code |
|--------|-----------------------------------|-----------------------------------------------------------------------|-------|-------------|
| GET    | `/api/v1/admin/users`             | Get all users in the system. Admin role required.                      | ADMIN | 200 |
| GET    | `/api/v1/admin/users/{id}`        | Get user details by ID. Admin role required.                           | ADMIN | 200 |
| PATCH  | `/api/v1/admin/users/{id}/role`   | Update user role (USER, MANAGER, ADMIN). Admin role required.          | ADMIN | 200 |
| PATCH  | `/api/v1/admin/users/{id}/activate`| Activate/deactivate user (ACTIVE, DISABLED, PENDING, BANNED).          | ADMIN | 200 |
| DELETE | `/api/v1/admin/users/{id}`        | Delete user permanently. Admin role required.                          | ADMIN | 200 |

---

# Team Management API Endpoints

| Method | Endpoint                          | Description                                                           | Roles | Status Code |
|--------|-----------------------------------|-----------------------------------------------------------------------|-------|-------------|
| POST   | `/api/v1/teams`                   | Create a new team. All authenticated users can create teams.           | USER, MANAGER, ADMIN | 201 |
| GET    | `/api/v1/teams/{teamId}`          | Get team details. User must be a team member.                          | USER, MANAGER, ADMIN | 200 |
| GET    | `/api/v1/teams`                   | Get user's teams. Returns all teams the user belongs to.               | USER, MANAGER, ADMIN | 200 |
| PUT    | `/api/v1/teams/{teamId}`          | Update team details. User must be team owner or admin.                 | USER, MANAGER, ADMIN | 200 |
| DELETE | `/api/v1/teams/{teamId}`          | Delete team. User must be team owner or admin.                         | USER, MANAGER, ADMIN | 200 |
| GET    | `/api/v1/teams/all`               | Get all teams in system. Admin role required.                          | ADMIN | 200 |

---

# Project Management API Endpoints

| Method | Endpoint                          | Description                                                           | Roles | Status Code |
|--------|-----------------------------------|-----------------------------------------------------------------------|-------|-------------|
| POST   | `/api/v1/projects`                | Create a new project. Manager or Admin role required.                  | MANAGER, ADMIN | 201 |
| GET    | `/api/v1/projects/{projectId}`    | Get project details. User must be project member.                      | USER, MANAGER, ADMIN | 200 |
| GET    | `/api/v1/projects/team/{teamId}`  | Get all projects for a team. User must be team member.                 | USER, MANAGER, ADMIN | 200 |
| GET    | `/api/v1/projects`                | Get user's projects. Returns all projects user belongs to.             | USER, MANAGER, ADMIN | 200 |
| PUT    | `/api/v1/projects/{projectId}`    | Update project details. Manager or Admin role required.                | MANAGER, ADMIN | 200 |
| DELETE | `/api/v1/projects/{projectId}`    | Delete project. Manager or Admin role required.                        | MANAGER, ADMIN | 200 |

---

# Task Management API Endpoints

| Method | Endpoint                          | Description                                                           | Roles | Status Code |
|--------|-----------------------------------|-----------------------------------------------------------------------|-------|-------------|
| POST   | `/api/v1/tasks`                   | Create a new task. Manager or Admin role required.                     | MANAGER, ADMIN | 201 |
| GET    | `/api/v1/tasks/{taskId}`          | Get task details.                                                       | USER, MANAGER, ADMIN | 200 |
| GET    | `/api/v1/tasks/project/{projectId}`| Get all tasks for a project.                                           | USER, MANAGER, ADMIN | 200 |
| GET    | `/api/v1/tasks`                   | Get user's assigned tasks.                                              | USER, MANAGER, ADMIN | 200 |
| PUT    | `/api/v1/tasks/{taskId}`          | Update task details. Manager or Admin role required.                   | MANAGER, ADMIN | 200 |
| DELETE | `/api/v1/tasks/{taskId}`          | Delete task. Manager or Admin role required.                           | MANAGER, ADMIN | 200 |
| PUT    | `/api/v1/tasks/{taskId}/assign/{assigneeId}`| Assign task to user. Manager or Admin role required. | MANAGER, ADMIN | 200 |
| PUT    | `/api/v1/tasks/{taskId}/status/{status}`| Update task status. All roles can update if assigned to task. | USER, MANAGER, ADMIN | 200 |

---

# Task Comments API Endpoints

| Method | Endpoint                          | Description                                                           | Roles | Status Code |
|--------|-----------------------------------|-----------------------------------------------------------------------|-------|-------------|
| POST   | `/api/v1/comments`                | Add comment to task.                                                    | USER, MANAGER, ADMIN | 201 |
| GET    | `/api/v1/comments/{commentId}`    | Get comment details.                                                    | USER, MANAGER, ADMIN | 200 |
| GET    | `/api/v1/comments/task/{taskId}`  | Get all comments for a task.                                            | USER, MANAGER, ADMIN | 200 |
| PUT    | `/api/v1/comments/{commentId}`    | Update comment. User can only edit own comments.                        | USER, MANAGER, ADMIN | 200 |
| DELETE | `/api/v1/comments/{commentId}`    | Delete comment. User can only delete own comments.                      | USER, MANAGER, ADMIN | 200 |

---

# Notification API Endpoints

| Method | Endpoint                          | Description                                                           | Roles | Status Code |
|--------|-----------------------------------|-----------------------------------------------------------------------|-------|-------------|
| GET    | `/api/v1/notifications`           | Get user notifications.                                                 | USER, MANAGER, ADMIN | 200 |
| PUT    | `/api/v1/notifications/{notificationId}/read`| Mark notification as read. | USER, MANAGER, ADMIN | 200 |
| PUT    | `/api/v1/notifications/read-all`  | Mark all notifications as read.                                        | USER, MANAGER, ADMIN | 200 |
| DELETE | `/api/v1/notifications/{notificationId}`| Delete notification. | USER, MANAGER, ADMIN | 200 |

---

# Activity Logs API Endpoints (Admin Only)

| Method | Endpoint                          | Description                                                           | Roles | Status Code |
|--------|-----------------------------------|-----------------------------------------------------------------------|-------|-------------|
| GET    | `/api/v1/activity-logs/entity/{entityId}`| Get activity logs for entity. Admin role required. | ADMIN | 200 |
| GET    | `/api/v1/activity-logs/user/{userId}`| Get activity logs by user. Admin role required. | ADMIN | 200 |
| GET    | `/api/v1/activity-logs/project/{projectId}`| Get project activity logs. Admin role required. | ADMIN | 200 |

---

## Response Format

All API responses follow a standard format:

\`\`\`json
{
"success": true,
"statusCode": 200,
"message": "Operation successful",
"data": {}
}
\`\`\`

### Error Response Format

\`\`\`json
{
"success": false,
"statusCode": 400,
"message": "Error message",
"data": null
}
\`\`\`

---

## Authentication

All protected endpoints require a JWT token in the `Authorization` header:

\`\`\`
Authorization: Bearer {jwt_token}
\`\`\`

---

## HTTP Status Codes

- `200`: Success
- `201`: Created
- `400`: Bad Request
- `401`: Unauthorized
- `403`: Forbidden (Insufficient permissions)
- `404`: Not Found
- `500`: Internal Server Error
