package com.iteam.buget.core.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendAccountValidation(String to, String fullName) {
        send(to,
             "Account validated — Budget App",
             "Hello " + fullName + ",\n\nYour account has been validated. You can now log in.\n\nBudget App Team");
    }

    public void sendAccountDeletionConfirmation(String to, String fullName) {
        send(to,
             "Account deletion confirmed — Budget App",
             "Hello " + fullName + ",\n\nYour account deletion request has been approved. Your data has been removed.\n\nBudget App Team");
    }

    public void sendBudgetAlert(String to, String budgetName, String alertMessage) {
        send(to,
             "Budget alert: " + budgetName,
             "Hello,\n\nAlert on your budget \"" + budgetName + "\":\n" + alertMessage + "\n\nBudget App Team");
    }

    private void send(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
