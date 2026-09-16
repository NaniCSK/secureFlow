# Google OAuth2 / OIDC Authentication Flow

## Overview

SecureFlow supports Google authentication using Spring Security OAuth2 Client
and OpenID Connect (OIDC).

Google is used as the external Identity Provider (IdP). After Google
successfully authenticates the user, SecureFlow creates or links a local user
account and issues its own JWT access token and refresh token.

## Authentication Flow

```text
User
 |
 | Login with Google
 v
SecureFlow Auth Service
 |
 | /oauth2/authorization/google
 v
Google
 |
 | User authenticates
 v
Google
 |
 | Authorization Code
 v
SecureFlow
 |
 | Exchange authorization code
 v
Google
 |
 | OIDC identity information
 v
OAuth2AuthenticationSuccessHandler
 |
 | email, name, provider ID
 v
AuthService
 |
 | Find existing user or create new user
 v
SecureFlow User
 |
 | Generate application JWT
 v
Access Token + Refresh Token
 |
 v
SecureFlow Protected APIs