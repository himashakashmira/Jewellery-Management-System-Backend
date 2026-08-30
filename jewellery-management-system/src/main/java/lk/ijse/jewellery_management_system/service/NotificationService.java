package lk.ijse.jewellery_management_system.service;

public interface NotificationService {
    void notifyCustomer(Integer userId, String email, String message);
}
