# 🏗️ TaskMaster Database Schema (Design View)

**Database:** PostgreSQL  
**Architecture:** Multi-tenant (Teams → Projects → Tasks)  
**Core Entities:** User, Role, Team, Project, Task, Comment, Notification, Invitation, ActivityLog

---

## 👤 USERS

| Field                            | Type                                 | Constraints       | Description                                |
|----------------------------------|--------------------------------------|-------------------|--------------------------------------------|
| id                               | UUID                                 | PK                | Unique user identifier                     |
| username                         | VARCHAR(50)                          | UNIQUE, NOT NULL  | Login name                                 |
| email                            | CITEXT                               | UNIQUE, NOT NULL  | Email (case-insensitive)                   |
| password_hash                    | TEXT                                 | NOT NULL          | BCrypt hash                                |
| first_name                       | VARCHAR(70)                          | NOT NULL          | User’s first name                          |
| last_name                        | VARCHAR(70)                          | NOT NULL          | User’s last name                           |
| bio                              | TEXT                                 |                   | Profile bio                                |
| email_verification_code          | VARCHAR(6)                           |                   | email verification code                    |
| email_verification_code_expireAt | TIMESTAMPTZ                          |                   | time that email verification code expire   |
| status                           | ENUM(active,disabled,pending,banned) | DEFAULT 'pending' | Account state                              |
| email_verified                   | BOOLEAN                              | DEFAULT FALSE     | Email verification status                  |
| avatar_url                       | TEXT                                 |                   | Profile image                              |
| metadata                         | JSONB                                |                   | Additional flexible data                   |
| password_reset_token             | TEXT                                 |                   | for forgot password to reset via emil      |
| password_reset_token_expireAt    | TIMESTAMPTZ                          |                   | time that reset password token code expire |
| created_at                       | TIMESTAMPTZ                          | DEFAULT now()     | Created timestamp                          |
| updated_at                       | TIMESTAMPTZ                          | DEFAULT now()     | Updated timestamp                          |
| last_login_at                    | TIMESTAMPTZ                          |                   | Last login                                 |

**Relations:**

- User → Roles: Many-to-many via `user_roles`
- User → Teams: Many-to-many via `team_members`
- User → Projects: Many-to-many via `project_members`
- User → Tasks: One-to-many as **assignee** or **reporter**

---

## 🛡️ ROLES

| Field       | Type                     | Constraints | Description |
| ----------- | ------------------------ | ----------- | ----------- |
| id          | SERIAL                   | PK          | Role ID     |
| name        | ENUM(ADMIN,MANAGER,USER) | UNIQUE      | System role |
| description | TEXT                     |             | Description |

**Relations:**
- `user_roles (user_id, role_id)` — Assigns roles to users

---

## 👥 TEAMS

| Field       | Type                 | Constraints   | Description        |
| ----------- | -------------------- | ------------- | ------------------ |
| id          | UUID                 | PK            | Team ID            |
| name        | VARCHAR(200)         | NOT NULL      | Team name          |
| slug        | VARCHAR(200)         | UNIQUE        | Friendly team slug |
| description | TEXT                 |               | Team info          |
| created_by  | UUID (FK → users.id) |               | Creator            |
| metadata    | JSONB                |               | Extra data         |
| created_at  | TIMESTAMPTZ          | DEFAULT now() | Created date       |
| updated_at  | TIMESTAMPTZ          | DEFAULT now() | Updated date       |
| archived    | BOOLEAN              | DEFAULT FALSE | Soft delete flag   |

**Relations:**

- Team → Members: Many-to-many via `team_members`
- Team → Projects: One-to-many via `projects`

---

## 👥 TEAM_MEMBERS

| Field      | Type                          | Constraints       | Description      |
| ---------- | ----------------------------- | ----------------- | ---------------- |
| team_id    | UUID                          | PK, FK → teams.id | Team             |
| user_id    | UUID                          | PK, FK → users.id | Member           |
| role       | ENUM(owner,maintainer,member) | DEFAULT 'member'  | Team role        |
| invited_by | UUID                          | FK → users.id     | Who invited      |
| joined_at  | TIMESTAMPTZ                   | DEFAULT now()     | Joined timestamp |

---

## 📁 PROJECTS

| Field       | Type                                            | Constraints          | Description             |
| ----------- | ----------------------------------------------- | -------------------- | ----------------------- |
| id          | UUID                                            | PK                   | Project ID              |
| key         | VARCHAR(32)                                     | UNIQUE(team_id, key) | Short code (e.g. “CRM”)|
| name        | VARCHAR(255)                                    | NOT NULL             | Project name            |
| description | TEXT                                            |                      | Description             |
| status      | ENUM(planned,active,on_hold,completed,archived)| DEFAULT 'planned'    | Lifecycle               |
| visibility  | ENUM(private,team,public)                       | DEFAULT 'private'    | Access level            |
| owner_id    | UUID                                            | FK → users.id        | Project owner           |
| team_id     | UUID                                            | FK → teams.id        | Owning team             |
| start_date  | DATE                                            |                      | Planned start           |
| due_date    | DATE                                            |                      | Deadline                |
| metadata    | JSONB                                           |                      | Custom project fields   |
| created_by  | UUID                                            | FK → users.id        | Created by              |
| created_at  | TIMESTAMPTZ                                     | DEFAULT now()        | Created date            |
| updated_at  | TIMESTAMPTZ                                     | DEFAULT now()        | Updated date            |
| archived    | BOOLEAN                                         | DEFAULT FALSE        | Soft delete flag        |

**Relations:**

- Project → Tasks: One-to-many
- Project → Members: Many-to-many via `project_members`
- Project → Team: Many projects per team

---

## 👤 PROJECT_MEMBERS

| Field      | Type        | Constraints           | Description     |
| ---------- | ----------- | --------------------- | --------------- |
| project_id | UUID        | PK, FK → projects.id  | Project         |
| user_id    | UUID        | PK, FK → users.id     | Member          |
| role       | VARCHAR(64) | DEFAULT 'contributor' | Role in project |
| joined_at  | TIMESTAMPTZ | DEFAULT now()         | When added      |

---

## ✅ TASKS

| Field              | Type                                                | Constraints             | Description            |
| ------------------ | --------------------------------------------------- | ----------------------- | ---------------------- |
| id                 | UUID                                                | PK                      | Task ID                |
| project_id         | UUID                                                | FK → projects.id        | Project link           |
| key                | VARCHAR(64)                                         | UNIQUE(project_id, key) | Task key (e.g. PROJ-1)|
| title              | VARCHAR(500)                                        | NOT NULL                | Task title             |
| description        | TEXT                                                |                         | Task details           |
| status             | ENUM(todo,in_progress,blocked,review,done,archived)| DEFAULT 'todo'          | Workflow status        |
| priority           | ENUM(lowest,low,medium,high,highest)               | DEFAULT 'medium'        | Priority level         |
| assignee_id        | UUID                                                | FK → users.id           | Assigned user          |
| reporter_id        | UUID                                                | FK → users.id           | Reporter               |
| parent_task_id     | UUID                                                | FK → tasks.id           | Subtask link           |
| estimate_minutes   | INT                                                 |                         | Estimated effort       |
| time_spent_minutes | INT                                                 | DEFAULT 0               | Logged time            |
| start_date         | DATE                                                |                         | Start date             |
| due_date           | DATE                                                |                         | Due date               |
| tags               | TEXT[]                                              |                         | Labels                 |
| custom_fields      | JSONB                                               |                         | Dynamic fields         |
| created_by         | UUID                                                | FK → users.id           | Creator                |
| created_at         | TIMESTAMPTZ                                         | DEFAULT now()           | Created timestamp      |
| updated_at         | TIMESTAMPTZ                                         | DEFAULT now()           | Updated timestamp      |
| archived           | BOOLEAN                                             | DEFAULT FALSE           | Soft delete            |

**Relations:**

- Task → Project (many tasks per project)
- Task → Comments (one-to-many)
- Task → Attachments (one-to-many)
- Task → ActivityLogs (many-to-one tracking)

---

## 💬 TASK_COMMENTS

| Field             | Type        | Constraints           | Description      |
| ----------------- | ----------- | --------------------- | ---------------- |
| id                | UUID        | PK                    | Comment ID       |
| task_id           | UUID        | FK → tasks.id         | Task reference   |
| author_id         | UUID        | FK → users.id         | Comment author   |
| content           | TEXT        | NOT NULL              | Comment text     |
| parent_comment_id | UUID        | FK → task_comments.id | Threaded comment |
| attachments       | JSONB       |                       | Attached files   |
| created_at        | TIMESTAMPTZ | DEFAULT now()         | Created date     |
| edited_at         | TIMESTAMPTZ |                       | Edit date        |

---

## 📎 ATTACHMENTS

| Field        | Type          | Constraints           | Description             |
| ------------ | ------------- | --------------------- | ----------------------- |
| id           | UUID          | PK                    | Attachment ID           |
| owner_id     | UUID          | FK → users.id         | File owner              |
| project_id   | UUID          | FK → projects.id      | Related project         |
| task_id      | UUID          | FK → tasks.id         | Related task            |
| comment_id   | UUID          | FK → task_comments.id | Related comment         |
| filename     | VARCHAR(1024) |                       | File name               |
| mime_type    | VARCHAR(255)  |                       | MIME type               |
| file_size    | BIGINT        |                       | File size bytes         |
| storage_path | TEXT          | NOT NULL              | File path or object key |
| created_at   | TIMESTAMPTZ   | DEFAULT now()         | Upload date             |

---

## 🔔 NOTIFICATIONS

| Field      | Type                       | Constraints         | Description       |
| ---------- | -------------------------- | ------------------- | ----------------- |
| id         | UUID                       | PK                  | Notification ID   |
| user_id    | UUID                       | FK → users.id       | Receiver          |
| actor_id   | UUID                       | FK → users.id       | Triggered by      |
| type       | VARCHAR(100)               | NOT NULL            | Notification type |
| channel    | ENUM(email,websocket,push) | DEFAULT 'websocket' | Delivery channel  |
| payload    | JSONB                      |                     | Notification data |
| is_read    | BOOLEAN                    | DEFAULT FALSE       | Read status       |
| created_at | TIMESTAMPTZ                | DEFAULT now()       | Created date      |

---

## ✉️ INVITATIONS

| Field         | Type                                    | Constraints       | Description        |
| ------------- | --------------------------------------- | ----------------- | ------------------ |
| id            | UUID                                    | PK                | Invitation ID      |
| invitee_email | CITEXT                                  | NOT NULL          | Target email       |
| inviter_id    | UUID                                    | FK → users.id     | Sender             |
| team_id       | UUID                                    | FK → teams.id     | Team invited to    |
| project_id    | UUID                                    | FK → projects.id  | Project invited to |
| token         | UUID                                    | NOT NULL          | Invitation token   |
| status        | ENUM(pending,accepted,rejected,expired) | DEFAULT 'pending' | Invitation state   |
| role          | VARCHAR(64)                             |                   | Assigned role      |
| expires_at    | TIMESTAMPTZ                             |                   | Expiry date        |
| created_at    | TIMESTAMPTZ                             | DEFAULT now()     | Created timestamp  |

---

## 📜 ACTIVITY_LOGS

| Field       | Type         | Constraints   | Description                       |
| ----------- | ------------ | ------------- | --------------------------------- |
| id          | BIGSERIAL    | PK            | Log entry                         |
| actor_id    | UUID         | FK → users.id | Who did it                        |
| action      | VARCHAR(150) | NOT NULL      | Action type (e.g. “task.updated”)|
| entity_type | VARCHAR(64)  |               | Type of entity                    |
| entity_id   | UUID         |               | Target entity                     |
| data        | JSONB        |               | Extra info/diff                   |
| ip          | INET         |               | IP address                        |
| user_agent  | TEXT         |               | Client agent                      |
| created_at  | TIMESTAMPTZ  | DEFAULT now() | Time of action                    |

---

## 🔗 Relationships Overview (ER Design Summary)


# 🔗 TaskMaster Entity Relationships

| From Entity | Relationship        | To Entity      | Cardinality     |
|-------------|---------------------|----------------|-----------------|
| Users       | user_roles          | Roles          | 1──< >──M       |
| Users       | team_members        | Teams          | 1──< >──M       |
| Teams       | projects            | Projects       | 1──M            |
| Projects    | project_members     | Users          | 1──< >──M       |
| Projects    | tasks               | Tasks          | 1──M            |
| Tasks       | comments            | Comments       | 1──M            |
| Tasks       | attachments         | Attachments    | 1──M            |
| Users       | notifications       | Notifications  | 1──M            |
| Users       | activity_logs       | ActivityLogs   | 1──M            |
