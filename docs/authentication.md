# SecureFlow Authentication Architecture

## Overview

SecureFlow uses a centralized authentication architecture built around the
Auth Service. All client requests enter through the API Gateway, which
routes requests to the appropriate microservice.

The platform is designed to support:

- Email & Password authentication
- JWT Authentication
- Google OAuth 2.0
- OpenID Connect (OIDC)
- Single Sign-On (SSO)
- Refresh Tokens
- Role-Based Access Control (RBAC)
- Multi-Tenant Authorization

---

# Authentication Flow

## Local Authentication

```text
Angular Frontend
        │
        ▼
   API Gateway
        │
        ▼
   Auth Service
        │
        ▼
 PostgreSQL (auth_db)
        │
        ▼
 Generate JWT
        │
        ▼
 Angular stores token
```

### Flow

1. User enters email and password.
2. API Gateway forwards the request to Auth Service.
3. Auth Service validates credentials.
4. Password is verified using BCrypt.
5. A JWT Access Token is generated.
6. The token is returned to the frontend.
7. The frontend includes the JWT in future API requests.

---

# Google OAuth 2.0 / OIDC Flow

```text
Angular
    │
    ▼
Google Identity Provider
    │
    ▼
Authorization Code
    │
    ▼
Auth Service
    │
    ▼
Create / Link User
    │
    ▼
Generate SecureFlow JWT
    │
    ▼
Angular Dashboard
```

### OAuth Flow

1. User chooses **Continue with Google**.
2. Angular redirects the user to Google.
3. Google authenticates the user.
4. Google returns an Authorization Code.
5. Auth Service exchanges the code for user information.
6. SecureFlow creates or links the application user.
7. SecureFlow issues its own JWT.
8. Angular uses the SecureFlow JWT for future requests.

---

# Why Use SecureFlow JWT?

Even after Google authentication, SecureFlow does **not** use Google's
access token for internal APIs.

Instead:

- Google verifies the user's identity.
- SecureFlow creates its own application JWT.
- Internal microservices trust only SecureFlow JWTs.

This provides complete control over authorization and tenant management.

---

# JWT Request Flow

```text
Angular
    │
Authorization: Bearer <JWT>
    │
    ▼
API Gateway
    │
JWT Validation
    │
    ▼
Microservice
```

The JWT will be attached to every protected request.

Example:

```http
GET /api/projects

Authorization: Bearer eyJhbGciOi...
```

---

# JWT Claims

The SecureFlow JWT will contain:

| Claim | Purpose |
|-------|----------|
| sub | User identifier |
| email | User email |
| tenantId | Organization identifier |
| roles | User roles |
| iat | Issued time |
| exp | Expiration time |

Example:

```json
{
  "sub": "user-123",
  "email": "john@example.com",
  "tenantId": "org-001",
  "roles": [
    "ORG_ADMIN"
  ]
}
```

---

# API Gateway Responsibilities

The API Gateway is responsible for:

- Routing requests
- Validating JWTs
- Forwarding authenticated requests
- Rejecting invalid tokens
- Adding security headers
- Request logging

Business logic **does not** live inside the Gateway.

---

# Auth Service Responsibilities

The Auth Service owns authentication and identity.

Responsibilities include:

- User registration
- Login
- Password hashing
- JWT generation
- Refresh tokens
- Google OAuth
- OIDC integration
- SSO
- User identity management

Database:

```text
auth_db
```

---

# Refresh Token Design

SecureFlow will use two tokens.

| Token | Lifetime |
|-------|----------|
| Access Token | 15 minutes |
| Refresh Token | 7 days |

Flow:

```text
Login
   │
   ▼
Access Token (15 min)

Refresh Token (7 days)
        │
        ▼
Request new Access Token
```

---

# Role-Based Authorization

Authorization is based on roles stored inside the JWT.

Example roles:

- USER
- PROJECT_MANAGER
- ORG_ADMIN

Example:

```text
JWT
 ├── tenantId
 ├── userId
 └── roles
```

Each microservice can authorize requests without querying the Auth Service.

---

# Multi-Tenant Security

Every authenticated user belongs to an organization.

```text
Organization
      │
      ├── Users
      └── Projects
```

The `tenantId` inside the JWT ensures users can only access resources
belonging to their organization.

Example:

```text
User A
tenant = Company A

Cannot access

Company B Projects
```

---

# Planned Security Features

The following features will be implemented during the project:

- JWT Authentication
- Refresh Tokens
- Google OAuth 2.0
- OpenID Connect
- SSO
- RBAC
- Multi-Tenant Authorization
- API Gateway JWT validation
- Secure password hashing using BCrypt

---

# Authentication Architecture Summary

```text
                    Google OAuth
                          │
                          ▼
                   Auth Service
                          │
                    Generate JWT
                          │
                          ▼
                     API Gateway
                          │
        ┌─────────────────┼─────────────────┐
        ▼                 ▼                 ▼
   User Service     Project Service   Notification
                                              Service
```

The Auth Service is the single source of truth for identity and
authentication across SecureFlow.