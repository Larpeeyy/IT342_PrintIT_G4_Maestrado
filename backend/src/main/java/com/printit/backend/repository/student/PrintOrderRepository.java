package com.printit.backend.repository.student;

import com.printit.backend.entity.User;
import com.printit.backend.entity.student.PrintOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrintOrderRepository extends JpaRepository<PrintOrder, Long> {

    List<PrintOrder> findByStudentOrderByCreatedAtDesc(User student);

    long countByStudent(User student);

    long countByStudentAndStatus(User student, String status);

    Optional<PrintOrder> findByIdAndStudent(Long id, User student);
}