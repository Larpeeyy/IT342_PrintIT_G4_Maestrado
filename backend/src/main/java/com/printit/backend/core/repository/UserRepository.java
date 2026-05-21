package com.printit.backend.core.repository;

import com.printit.backend.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    long countByRole(String role);

    long countByRoleAndApprovalStatus(String role, String approvalStatus);

    List<User> findByRoleAndApprovalStatusOrderByIdDesc(String role, String approvalStatus);

    List<User> findByRole(String role);

    List<User> findByRoleAndApprovalStatus(String role, String approvalStatus);
}