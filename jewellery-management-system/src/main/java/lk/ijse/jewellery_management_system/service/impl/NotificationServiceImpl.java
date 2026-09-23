package lk.ijse.jewellery_management_system.service.impl;

import lk.ijse.jewellery_management_system.entity.Notification;
import lk.ijse.jewellery_management_system.entity.User;
import lk.ijse.jewellery_management_system.repository.NotificationRepository;
import lk.ijse.jewellery_management_system.repository.UserRepository;
import lk.ijse.jewellery_management_system.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender; // for sending emails
    private final UserRepository userRepository;

    @Override
    public void notifyCustomer(Integer userId, String email, String message) {
        notifyCustomer(userId, email, "AURUM JEWELS | Repair Lounge Docket Update", message);
    }

    @Override
    public void notifyCustomer(Integer userId, String email, String subject, String message) {
        // save notification to database if valid user account exists
        if (userId != null) {
            userRepository.findById(userId).ifPresent(user -> {
                Notification notification = Notification.builder()
                        .message(message)
                        .user(user)
                        .createdAt(LocalDateTime.now())
                        .isRead(false)
                        .build();
                notificationRepository.save(notification);
            });
        }

        sendEmail(email, subject, message);
    }

    @Override
    public void sendEmail(String email, String subject, String message) {
        // send automated email to customer/member
        if (email != null && !email.isBlank()) {
            try {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setFrom("himashakashmira7@gmail.com");
                mailMessage.setTo(email.trim());
                mailMessage.setSubject(subject != null ? subject : "AURUM JEWELS Notification");
                mailMessage.setText(message);
                mailSender.send(mailMessage);
                System.out.println("[Automated Email] Successfully dispatched email to: " + email + " | Subject: " + subject);
            } catch (Exception e) {
                System.err.println("[Automated Email] Notification failed/logged: " + e.getMessage());
            }
        }
    }
}