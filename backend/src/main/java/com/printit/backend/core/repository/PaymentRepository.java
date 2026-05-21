package com.printit.backend.core.repository;

import com.printit.backend.core.entity.User;
import com.printit.backend.core.entity.Payment;
import com.printit.backend.core.entity.PrintOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.order.student = :student ORDER BY p.createdAt DESC")
    List<Payment> findByStudentOrderByCreatedAtDesc(User student);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.order.student = :student AND p.status = 'Completed'")
    BigDecimal sumCompletedPaymentsByStudent(User student);

    @Query("SELECT COALESCE(SUM(p.order.totalAmount), 0) FROM Payment p WHERE p.order.student = :student AND p.order.status = 'Completed'")
    BigDecimal sumCompletedOrderAmountsByStudent(User student);

    List<Payment> findAllByOrderByCreatedAtDesc();

    Optional<Payment> findByOrder(PrintOrder order);

    List<Payment> findByStatusOrderByCreatedAtDesc(String status);

    @Query("SELECT p FROM Payment p WHERE p.id = :paymentId AND p.order.student = :student")
    Optional<Payment> findByIdAndStudent(Long paymentId, User student);
}