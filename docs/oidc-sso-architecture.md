# OIDC / SSO Architecture

## Overview

SecureFlow uses OpenID Connect (OIDC) to support authentication through
external Identity Providers.

The project currently supports:

- Google as an external Identity Provider
- Keycloak as an Identity and Access Management (IAM) platform
- Local email/password authentication

Google and Keycloak authenticate the user, while SecureFlow creates or
links a local user account and issues its own JWT access token and refresh
token.

## Authentication Providers

### Google

Google acts as an external Identity Provider.

```text
User
  |
  | Login with Google
  v
Google
  |
  | OIDC authentication
  v
SecureFlow Auth Service