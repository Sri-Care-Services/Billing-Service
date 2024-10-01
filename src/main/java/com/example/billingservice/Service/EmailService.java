package com.example.billingservice.Service;

import com.example.billingservice.Entity.Payment;
import com.example.billingservice.Repository.PaymentHistoryRepository;
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
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final PaymentHistoryRepository paymentHistoryRepository;

    public EmailService(JavaMailSender mailSender, PaymentHistoryRepository paymentHistoryRepository) {
        this.mailSender = mailSender;
        this.paymentHistoryRepository = paymentHistoryRepository;
    }

    public void sendMonthlyStatement(Long userId) throws MessagingException, IOException, jakarta.mail.MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo("kokularajh32@gmail.com");

        LocalDate currentDate = LocalDate.now();

        String monthName = currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String subject = "Monthly Statement of " + monthName;
        helper.setSubject(subject);

        String body = "Monthly Statement of " + monthName;
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
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 700);

            contentStream.showText("Monthly Statement                                             Sri Care");
            contentStream.newLineAtOffset(0, -100);

            List<Payment> paymentHistory = paymentHistoryRepository.findByUserId(userId);

            if (paymentHistory.isEmpty()) {
                contentStream.endText();
                contentStream.close();
                document.save(byteArrayOutputStream);
                return;
            }

            for(Payment payment : paymentHistory) {
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.newLineAtOffset(0, -30);
                contentStream.showText("Package: " + payment.getTitle());

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Amount: " + payment.getAmount() + "                                                                                                                                       " + payment.getDate());
//                contentStream.newLineAtOffset(0, -20);
//                contentStream.showText("Date: " + payment.getDate());
            }

            contentStream.endText();
            contentStream.close();

            document.save(byteArrayOutputStream);
        }
    }
}
