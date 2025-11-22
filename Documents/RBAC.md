# 🔐 Role-Based Access Control (RBAC)

This system uses a **3-level role hierarchy** to ensure secure and organized access across all modules of the TaskMaster backend.

Roles include:

- **Admin** → System-wide access
- **Project Manager** → Controls individual projects, tasks, and members
- **User** → Regular contributor with limited permissions

---

##  1. User Management Permissions

| Action | Admin | Project Manager | User |
|--------|:-----:|:---------------:|:----:|
| Register account | ✔ | ✔ | ✔ |
| Login | ✔ | ✔ | ✔ |
| Update own profile | ✔ | ✔ | ✔ |
| View any user | ✔ | ❌ | ❌ |
| Delete any user | ✔ | ❌ | ❌ |
| Deactivate/Activate users | ✔ | ❌ | ❌ |

---

##  2. Project Permissions

| Action | Admin | Project Manager | User |
|--------|:-----:|:---------------:|:----:|
| Create project | ✔ | ✔ | ❌ |
| View all projects | ✔ | ❌ | ❌ |
| View project (where member) | ✔ | ✔ | ✔ |
| Update project info | ✔ | ✔ | ❌ |
| Delete project | ✔ | ✔ | ❌ |
| Add members | ✔ | ✔ | ❌ |
| Remove members | ✔ | ✔ | ❌ |
| Change member roles | ✔ | ✔ | ❌ |
| View project members | ✔ | ✔ | ✔ |

---

##  3. Task Permissions

| Action | Admin | Project Manager | User |
|--------|:-----:|:---------------:|:----:|
| Create task | ✔ | ✔ | ✔ *(only in assigned projects)* |
| Assign task to user | ✔ | ✔ | ❌ |
| Update task details | ✔ | ✔ | ✔ *(own tasks only)* |
| Change task status | ✔ | ✔ | ✔ *(if assigned)* |
| Delete task | ✔ | ✔ | ❌ |
| View tasks | ✔ | ✔ | ✔ |
| Set priority/due dates | ✔ | ✔ | ❌ |

---

##  4. Comment Permissions

| Action | Admin | Project Manager | User |
|--------|:-----:|:---------------:|:----:|
| Add comment | ✔ | ✔ | ✔ |
| Edit own comment | ✔ | ✔ | ✔ |
| Edit others’ comments | ✔ | ✔ | ❌ |
| Delete own comment | ✔ | ✔ | ✔ |
| Delete any comment | ✔ | ✔ | ❌ |
| View comments | ✔ | ✔ | ✔ |

---

##  5. Notification & Email Permissions

| Action | Admin | Project Manager | User |
|--------|:-----:|:---------------:|:----:|
| Receive notifications | ✔ | ✔ | ✔ |
| Send email invitations | ✔ | ✔ | ❌ |
| View email logs | ✔ | ❌ | ❌ |
| Trigger system notifications | ✔ | ✔ | ✔ *(based on actions)* |

---

##  6. Activity Log Permissions

| Action | Admin | Project Manager | User |
|--------|:-----:|:---------------:|:----:|
| View all activity logs | ✔ | ❌ | ❌ |
| View project activity logs | ✔ | ✔ | ✔ |
| Delete logs | ✔ | ❌ | ❌ |

---

##  7. Invitation & Membership Permissions

| Action | Admin | Project Manager | User |
|--------|:-----:|:---------------:|:----:|
| Send project invitations | ✔ | ✔ | ❌ |
| Accept project invite | ✔ | ✔ | ✔ |
| Decline project invite | ✔ | ✔ | ✔ |

---

#  Role Responsibility Summary

### 🛡 **Admin**
Full system authority: user management, projects, tasks, logs, notifications.

### 📂 **Project Manager**
Full control **within their projects**: tasks, members, roles, updates, and comments.

### 👤 **User**
Can collaborate on tasks inside assigned projects: update progress, comment, and view project data.

