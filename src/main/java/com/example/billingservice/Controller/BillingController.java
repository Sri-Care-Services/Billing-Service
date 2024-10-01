package com.example.billingservice.Controller;

import com.example.billingservice.DTO.PaymentRequestDTO;
import com.example.billingservice.Entity.Billing;
import com.example.billingservice.Entity.Payment;
import com.example.billingservice.Service.BillingService;
import com.example.billingservice.Service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/billing")
@Validated
@CrossOrigin
public class BillingController {

    @Autowired
    private BillingService billingService;

    @Autowired
    private EmailService emailService;

    // Get billing details/prices
    @GetMapping("/details/{billingId}")
    public ResponseEntity<Billing> getBillingDetails(@PathVariable Long billingId) {
        Billing billing = billingService.getBillingDetails(billingId);
        return ResponseEntity.ok(billing);
    }

    // Create billing details/prices
    @PostMapping("/details")
    public ResponseEntity<Billing> createBillingDetails(@RequestBody Billing billing) {
        billingService.createBillingDetails(billing);
        return ResponseEntity.ok(billing);
    }

    // Update billing details/prices
    @PutMapping("/details/{billingId}")
    public ResponseEntity<Billing> updateBillingDetails(@PathVariable Long billingId, @RequestBody Billing billing) {
        billing.setId(billingId);
        billingService.createBillingDetails(billing);
        return ResponseEntity.ok(billing);
    }

    // Make payments
    @PostMapping("/payment")
    public ResponseEntity<PaymentRequestDTO> makePayment(@Valid @RequestBody PaymentRequestDTO paymentRequest) {
        return ResponseEntity.status(201).body(billingService.processPayment(paymentRequest));
    }

    // Retrieve payment histories
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Payment>> getPaymentHistory(@PathVariable Long userId) {
        List<Payment> paymentHistories = billingService.getPaymentHistory(userId);
        return ResponseEntity.ok(paymentHistories);
    }

    // Generate and email monthly statements
    @PostMapping("/email/statement/{userId}")
    public ResponseEntity<String> sendMonthlyStatement(@PathVariable Long userId) {
        try {
            emailService.sendMonthlyStatement(userId);
            return ResponseEntity.ok("Monthly statement sent successfully.");
        } catch (MessagingException | IOException e) {
            return ResponseEntity.status(500).body("Error sending email: " + e.getMessage());
        } catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}