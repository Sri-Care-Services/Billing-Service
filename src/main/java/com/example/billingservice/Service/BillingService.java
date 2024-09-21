package com.example.billingservice.Service;

import com.billingService.billingService.DTO.PaymentRequestDTO;
import com.billingService.billingService.Entity.*;
import com.billingService.billingService.Repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BillingService {

    @Autowired
    private BillingDetailsRepository billingRepository;

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    public Billing getBillingDetails(Long billingId) {
        return billingRepository.findById(billingId).orElse(null);
    }

    public PaymentRequestDTO processPayment(@Valid PaymentRequestDTO paymentRequest) {
        Payment payment = new Payment();
        payment.setUserId(paymentRequest.getUserId());
        payment.setAmount(paymentRequest.getAmount());
        payment.setDate(paymentRequest.getDate());
        payment.setCreated_at(LocalDate.now());

        paymentHistoryRepository.save(payment);

        return paymentRequest;
    }

    public List<Payment> getPaymentHistory(Long userId) {
        return paymentHistoryRepository.findByUserId(userId);
    }

    public void createBillingDetails(Billing billing) {
        billingRepository.save(billing);
    }
}
