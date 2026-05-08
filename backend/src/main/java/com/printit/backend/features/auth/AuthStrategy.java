package com.printit.backend.features.auth;

import com.printit.backend.core.entity.User;

public interface AuthStrategy {
    User authenticate(AuthRequest request);
}