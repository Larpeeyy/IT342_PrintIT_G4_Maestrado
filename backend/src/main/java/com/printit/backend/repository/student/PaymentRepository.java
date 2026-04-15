package com.printit.backend.repository.student;

import com.printit.backend.entity.student.Payment;
import com.printit.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.order.student = :student ORDER BY p.createdAt DESC")
    List<Payment> findByStudentOrderByCreatedAtDesc(User student);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.order.student = :student AND p.status = 'Completed'")
    BigDecimal sumCompletedPaymentsByStudent(User student);
}