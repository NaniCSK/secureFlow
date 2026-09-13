package com.secureflow.authservice.security;

import java.util.UUID;

public record AuthenticatedUser(
        UUID id,
        String email
) {
}