package com.example.billingservice.Repository;

import com.example.billingservice.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
}
