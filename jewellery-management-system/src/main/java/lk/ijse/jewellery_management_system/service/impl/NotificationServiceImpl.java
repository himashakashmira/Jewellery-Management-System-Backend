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
        // 1. save notification to database
        User user = userRepository.findById(userId).orElseThrow();
        Notification notification = Notification.builder()
                .message(message)
                .user(user)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        // 2. send automated email to customer
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(email);
            mailMessage.setSubject("AURUM JEWELS - Update");
            mailMessage.setText(message);
            mailSender.send(mailMessage);
        } catch (Exception e) {
            // log error if email fails
            System.out.println("Email failed: " + e.getMessage());
        }
    }
}