package com.example.billingservice.Repository;

import com.billingService.billingService.Entity.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingDetailsRepository extends JpaRepository<Billing, Long> {
}
