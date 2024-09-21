package com.example.billingservice.Service;

import com.billingService.billingService.Entity.Payment;
import com.billingService.billingService.Repository.PaymentHistoryRepository;
import jakarta.mail.internet.MimeMessage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final PaymentHistoryRepository paymentHistoryRepository;

    public EmailService(JavaMailSender mailSender, PaymentHistoryRepository paymentHistoryRepository) {
        this.mailSender = mailSender;
        this.paymentHistoryRepository = paymentHistoryRepository;
    }

    public void sendMonthlyStatement(String toEmail, String subject, String body, Long userId) throws MessagingException, IOException, jakarta.mail.MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body, true);

        // Generate PDF statement
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        generatePDFStatement(byteArrayOutputStream, userId);

        // Attach the statement PDF
        helper.addAttachment("Monthly_Statement.pdf", new ByteArrayResource(byteArrayOutputStream.toByteArray()));

        mailSender.send(mimeMessage);
    }

    private void generatePDFStatement(ByteArrayOutputStream byteArrayOutputStream, Long userId) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Monthly Statement for User ID: " + userId);

            List<Payment> paymentHistory = paymentHistoryRepository.findByUserId(userId);

            if (paymentHistory.isEmpty()) {
                contentStream.endText();
                contentStream.close();
                document.save(byteArrayOutputStream);
                return;
            }

            for(Payment payment : paymentHistory) {
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Amount: " + payment.getAmount());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Date: " + payment.getDate());
            }

            contentStream.endText();
            contentStream.close();

            document.save(byteArrayOutputStream);
        }
    }
}
