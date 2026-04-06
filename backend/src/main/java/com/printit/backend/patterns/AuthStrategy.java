package com.printit.backend.patterns;

import com.printit.backend.dto.AuthRequest;
import com.printit.backend.entity.User;

public interface AuthStrategy {
    User authenticate(AuthRequest request);
}