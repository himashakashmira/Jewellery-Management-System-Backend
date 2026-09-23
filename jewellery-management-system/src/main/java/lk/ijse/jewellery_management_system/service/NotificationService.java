package lk.ijse.jewellery_management_system.service;

public interface NotificationService {
    void notifyCustomer(Integer userId, String email, String message);

    void notifyCustomer(Integer userId, String email, String subject, String message);

    void sendEmail(String toEmail, String subject, String message);
}
