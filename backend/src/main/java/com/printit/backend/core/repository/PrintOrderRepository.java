package com.printit.backend.core.repository;

import com.printit.backend.core.entity.User;
import com.printit.backend.core.entity.PrintOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PrintOrderRepository extends JpaRepository<PrintOrder, Long> {

    List<PrintOrder> findByStudentOrderByCreatedAtDesc(User student);

    Optional<PrintOrder> findByIdAndStudent(Long id, User student);

    long countByStudent(User student);

    long countByStudentAndStatus(User student, String status);

    List<PrintOrder> findAllByOrderByCreatedAtDesc();

    long countByStatus(String status);

    long countByStatusAndCreatedAtBetween(String status, LocalDateTime start, LocalDateTime end);

    @Query("SELECT p FROM PrintOrder p ORDER BY p.createdAt DESC")
    List<PrintOrder> findRecentOrders();
}