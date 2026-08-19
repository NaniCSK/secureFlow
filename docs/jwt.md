# SecureFlow JWT Design

## Overview

SecureFlow uses JSON Web Tokens (JWT) for authenticated access to
protected APIs.

The Auth Service is responsible for issuing application JWTs after
successful authentication.

JWTs will be used by the API Gateway and backend microservices to
authenticate requests and establish the user's identity, tenant and
roles.

---

## Access Token

The access token will be a signed JWT containing the authenticated
user's identity and authorization context.

### Example

```json
{
  "sub": "user-123",
  "email": "user@example.com",
  "tenantId": "org-001",
  "roles": [
    "ORG_ADMIN"
  ],
  "iat": 1750000000,
  "exp": 1750000900
}
```

---

## JWT Claims

| Claim | Type | Description |
|---|---|---|
| `sub` | String | Unique identifier of the authenticated user |
| `email` | String | Email address associated with the user |
| `tenantId` | String | Identifier of the user's organization/tenant |
| `roles` | Array | Roles assigned to the authenticated user |
| `iat` | Number | Token issuance timestamp |
| `exp` | Number | Token expiration timestamp |

---

## `sub` — Subject

The `sub` claim identifies the authenticated application user.

Example:

```json
{
  "sub": "user-123"
}
```

The value should be a stable application-level user identifier.

---

## `email`

The `email` claim identifies the user's email address.

Example:

```json
{
  "email": "user@example.com"
}
```

The email is primarily an identity attribute and should not be treated
as the authorization mechanism.

---

## `tenantId`

The `tenantId` claim identifies the organization to which the
authenticated user belongs.

Example:

```json
{
  "tenantId": "org-001"
}
```

This claim is important for SecureFlow's multi-tenant architecture.

Backend services will use the authenticated tenant context to ensure
that users cannot access resources belonging to another organization.

---

## `roles`

The `roles` claim contains the user's application roles.

Example:

```json
{
  "roles": [
    "ORG_ADMIN",
    "PROJECT_MANAGER"
  ]
}
```

Initial roles include:

- `USER`
- `PROJECT_MANAGER`
- `ORG_ADMIN`

Roles will be used for Role-Based Access Control (RBAC).

---

## `iat` — Issued At

The `iat` claim identifies when the JWT was issued.

Example:

```json
{
  "iat": 1750000000
}
```

The value uses a Unix timestamp.

---

## `exp` — Expiration

The `exp` claim defines when the access token expires.

Example:

```json
{
  "exp": 1750000900
}
```

SecureFlow will use short-lived access tokens.

Initial target:

```text
Access Token: 15 minutes
```

---

# Token Lifetime

SecureFlow will use two token types.

| Token | Target Lifetime | Purpose |
|---|---:|---|
| Access Token | 15 minutes | Authenticate API requests |
| Refresh Token | 7 days | Obtain a new access token |

The access token should be short-lived to reduce the impact of token
compromise.

---

# Authentication Flow

```text
User
 │
 ▼
Angular
 │
 ▼
Auth Service
 │
 ├── Validate credentials
 │
 ├── Authenticate using Google/OIDC
 │
 └── Generate SecureFlow JWT
 │
 ▼
Access Token
 │
 ▼
Angular
```

---

# Protected API Request

The Angular application sends the JWT using the HTTP Authorization
header.

```http
Authorization: Bearer <access-token>
```

Example:

```http
GET /api/projects
Authorization: Bearer eyJhbGciOi...
```

---

# JWT Validation

The intended request flow is:

```text
Angular
   │
   │ Authorization: Bearer JWT
   ▼
API Gateway
   │
   │ Validate JWT
   ▼
Microservice
```

The token must be:

- Structurally valid
- Correctly signed
- Not expired
- Issued by the expected SecureFlow authentication system

---

# Tenant Authorization

The `tenantId` claim provides the tenant context.

Example:

```text
JWT
 │
 ├── sub      → user-123
 ├── tenantId → org-001
 └── roles    → ORG_ADMIN
```

A request authenticated as:

```text
tenantId = org-001
```

must not be allowed to access resources belonging to:

```text
tenantId = org-002
```

Tenant authorization will ultimately be enforced by the backend
services.

---

# Role-Based Authorization

The `roles` claim provides the authorization context.

Example:

```text
roles = ["PROJECT_MANAGER"]
```

A Project Manager may be permitted to perform project-management
operations while an ordinary User may have more limited permissions.

The exact authorization rules will be implemented as the corresponding
features are developed.

---

# Refresh Token Flow

When the access token expires:

```text
Angular
   │
   │ Refresh Token
   ▼
Auth Service
   │
   │ Validate refresh token
   │
   ▼
New Access Token
   │
   ▼
Angular
```

Refresh tokens will be handled by the Auth Service rather than being
used as authorization credentials for normal business APIs.

---

# Security Principles

SecureFlow will follow these principles:

1. Access tokens are short-lived.
2. JWTs are signed by the Auth Service.
3. Protected APIs require authentication.
4. Tenant context is included in the authenticated security context.
5. Roles are used for authorization.
6. Passwords are never stored in JWTs.
7. Sensitive credentials are never included in JWT claims.
8. Refresh tokens are handled separately from access tokens.

---

# JWT Ownership

| Responsibility | Service |
|---|---|
| Authenticate users | Auth Service |
| Issue JWT | Auth Service |
| Validate JWT at API boundary | API Gateway |
| Enforce business authorization | Individual microservices |
| Maintain user identity | Auth/User services |

---

# Implementation Status

The JWT structure is currently a design specification.

Planned implementation includes:

- JWT generation
- JWT signature validation
- Spring Security integration
- Access-token expiration
- Refresh-token flow
- Role-based authorization
- Tenant-aware authorization
- API Gateway authentication