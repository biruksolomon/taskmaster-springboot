#  TaskMaster — Multi-User Task Management System (Spring Boot 3 + MySQL)

TaskMaster is a **backend API** built with **Java Spring Boot 3**, designed to manage **projects, tasks, teams, and roles** efficiently.  
It provides secure authentication, email communication, and real-time collaboration — all built with clean RESTful architecture.  
Ideal for **freelance, enterprise, or internal productivity solutions**.

---

## Features

✅ **User & Role Management**
- Secure JWT Authentication & Authorization
- Email verification on registration (via **Spring Boot Mail**)
- Password encryption with BCrypt
- Roles: Admin, Manager, User

✅ **Project Management**
- Create, update, and manage multiple projects
- Add or remove project members
- Track project deadlines and status

✅ **Task Management**
- CRUD operations for tasks
- Assign tasks to specific users
- Task priority, due dates, and progress tracking
- Filter tasks by status, priority, or assignee

✅ **Collaboration**
- Add comments on tasks
- Real-time notifications (via WebSocket or email)
- Activity logging for all user actions

✅ **Email Communication (JavaMail Integration)**
- **Email verification** during user registration
- **Password reset** emails for account recovery
- **Task or project notifications** to users
- Built using **Spring Boot Mail** with SMTP configuration (e.g., Gmail, SendGrid, or Mailtrap)

✅ **Technical Excellence**
- Built on **Spring Boot 3 + Java 17**
- **Spring Security + JWT** for authentication
- **Spring Data JPA + PostgreSQL** for persistence
- **OpenAPI / Swagger UI** for API documentation
- **Spring Boot Mail** for communication services
- **Lombok + MapStruct** for clean DTO and model mapping
- **Docker support** for easy deployment

---

## 🏗️ Architecture Overview

