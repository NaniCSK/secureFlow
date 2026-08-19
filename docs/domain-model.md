# SecureFlow Domain Model

## Overview

SecureFlow is a multi-tenant enterprise platform where users belong to
organizations and collaborate on projects.

The domain is divided across independent microservices. Each service
owns its business logic and persistent data.

---

## Organization

Represents a tenant/company using SecureFlow.

### Attributes

- `id`
- `name`
- `createdAt`

### Ownership

Owned by the User Service.

---

## User

Represents an authenticated user within an organization.

### Attributes

- `id`
- `organizationId`
- `email`
- `passwordHash`
- `provider`
- `status`
- `createdAt`

### Authentication Providers

A user may authenticate using:

- Local email/password authentication
- Google OAuth / OpenID Connect

---

## Role

Defines the permissions available to a user.

### Attributes

- `id`
- `name`

### Examples

- `USER`
- `PROJECT_MANAGER`
- `ORG_ADMIN`

Roles will be used for Role-Based Access Control (RBAC).

---

## Project

Represents a project belonging to an organization.

### Attributes

- `id`
- `organizationId`
- `name`
- `description`
- `status`
- `createdAt`

### Project Status

Potential states include:

- `PLANNING`
- `ACTIVE`
- `COMPLETED`
- `ARCHIVED`

---

## Task

Represents an individual task within a project.

### Attributes

- `id`
- `projectId`
- `assignedTo`
- `title`
- `description`
- `status`
- `createdAt`
- `dueDate`

### Task Status

Potential states include:

- `TODO`
- `IN_PROGRESS`
- `COMPLETED`
- `BLOCKED`

---

# Relationships

```text
Organization
    │
    ├──────────────► Users
    │
    └──────────────► Projects
                         │
                         └──────────────► Tasks

User
    │
    └──────────────► Roles

User
    │
    └──────────────► Assigned Tasks