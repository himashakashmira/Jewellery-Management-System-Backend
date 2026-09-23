package lk.ijse.jewellery_management_system.service.impl;

import lk.ijse.jewellery_management_system.dto.RepairDTO;
import lk.ijse.jewellery_management_system.dto.RepairStatsDTO;
import lk.ijse.jewellery_management_system.entity.Customer;
import lk.ijse.jewellery_management_system.entity.Repair;
import lk.ijse.jewellery_management_system.repository.CustomerRepository;
import lk.ijse.jewellery_management_system.repository.RepairRepository;
import lk.ijse.jewellery_management_system.service.NotificationService;
import lk.ijse.jewellery_management_system.service.RepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepairServiceImpl implements RepairService {

    private final RepairRepository repairRepository;
    private final CustomerRepository customerRepository;
    private final NotificationService notificationService; // inject notification service

    @Override
    @Transactional
    public void registerRepair(RepairDTO dto) {
        Customer customer = null;

        // 1. Resolve customer by ID if provided
        if (dto.getCustomerId() != null && dto.getCustomerId() > 0) {
            customer = customerRepository.findById(dto.getCustomerId()).orElse(null);
        }

        // 2. If not found by ID, look up by mobile contact number
        if (customer == null && dto.getCustomerContact() != null && !dto.getCustomerContact().isBlank()) {
            customer = customerRepository.findByContact(dto.getCustomerContact().trim()).orElse(null);
        }

        // 3. If customer does not exist, AUTO-REGISTER the customer into database!
        if (customer == null) {
            String clientName = (dto.getCustomerName() != null && !dto.getCustomerName().isBlank())
                    ? dto.getCustomerName().trim() : "Walk-in Restoration Patron";
            String clientPhone = (dto.getCustomerContact() != null && !dto.getCustomerContact().isBlank())
                    ? dto.getCustomerContact().trim() : "+94 77 123 4567";

            Customer newCustomer = Customer.builder()
                    .name(clientName)
                    .contact(clientPhone)
                    .email(dto.getCustomerEmail() != null && !dto.getCustomerEmail().isBlank() ? dto.getCustomerEmail().trim() : null)
                    .address(dto.getCustomerAddress() != null && !dto.getCustomerAddress().isBlank() ? dto.getCustomerAddress().trim() : null)
                    .loyaltyPoints(10)
                    .build();
            customer = customerRepository.save(newCustomer);
        } else {
            // Update email or address if newly provided
            boolean dirty = false;
            if ((customer.getEmail() == null || customer.getEmail().isBlank()) && dto.getCustomerEmail() != null && !dto.getCustomerEmail().isBlank()) {
                customer.setEmail(dto.getCustomerEmail().trim());
                dirty = true;
            }
            if ((customer.getAddress() == null || customer.getAddress().isBlank()) && dto.getCustomerAddress() != null && !dto.getCustomerAddress().isBlank()) {
                customer.setAddress(dto.getCustomerAddress().trim());
                dirty = true;
            }
            if (dirty) {
                customerRepository.save(customer);
            }
        }

        LocalDate handoverDate = (dto.getReceivedDate() != null) ? dto.getReceivedDate() : LocalDate.now();
        LocalDate targetDate = (dto.getReturnDate() != null) ? dto.getReturnDate() : handoverDate.plusDays(7);

        Repair repair = Repair.builder()
                .itemName(dto.getItemName())
                .description(dto.getDescription())
                .status("Received")
                .receivedDate(handoverDate)
                .returnDate(targetDate)
                .estimatedCost(dto.getEstimatedCost() != null ? dto.getEstimatedCost() : 0.0)
                .customer(customer)
                .build();
        Repair savedRepair = repairRepository.save(repair);

        // Send automated intake confirmation email if customer email is provided
        if (customer.getEmail() != null && !customer.getEmail().isBlank()) {
            Integer userId = (customer.getUser() != null) ? customer.getUser().getId() : null;
            String intakeMsg = "Dear " + customer.getName() + ",\n\n"
                    + "Your jewellery restoration docket has been officially opened at AURUM Atelier.\n\n"
                    + "• Docket: #REP-" + String.format("%04d", savedRepair.getId()) + "\n"
                    + "• Item Description: " + savedRepair.getItemName() + "\n"
                    + "• Date Handed Over: " + handoverDate + "\n"
                    + "• Target Completion: " + targetDate + "\n"
                    + "• Estimated Cost: LKR " + String.format("%,.2f", savedRepair.getEstimatedCost()) + "\n\n"
                    + "You will receive automated email updates whenever your piece advances to a new phase.\n\n"
                    + "AURUM JEWELS Atelier\n"
                    + "48 Galle Face Court, Colombo 03 | +94 11 234 5678";
            notificationService.notifyCustomer(userId, customer.getEmail(), intakeMsg);
        }
    }

    @Override
    @Transactional
    public void updateRepairStatus(Integer id, String status) {
        Repair repair = repairRepository.findById(id).orElseThrow();
        repair.setStatus(status);
        repairRepository.save(repair);

        // If status is "Ready", do not send email immediately.
        // It will be sent after staff confirms via the 'Notify' prompt button.
        if ("Ready".equalsIgnoreCase(status)) {
            return;
        }

        // Send automated email notification for in-progress milestones (Melting, Crafting, etc.)
        Customer cust = repair.getCustomer();
        if (cust != null) {
            String patronName = (cust.getName() != null && !cust.getName().isBlank()) ? cust.getName() : "Valued Patron";
            String recipientEmail = cust.getEmail();
            Integer userId = (cust.getUser() != null) ? cust.getUser().getId() : null;

            String statusMessage;
            if ("Crafting".equalsIgnoreCase(status)) {
                statusMessage = "Your jewellery repair (" + repair.getItemName() + ") is currently on the Master Artisan Bench undergoing fine setting and sculpting.";
            } else if ("Melting".equalsIgnoreCase(status)) {
                statusMessage = "Your jewellery repair (" + repair.getItemName() + ") is in the crucible chamber undergoing precision alloy restoration and laser joining.";
            } else {
                statusMessage = "Your jewellery repair (" + repair.getItemName() + ") status has been updated to: " + status + ".";
            }

            String emailBody = "Dear " + patronName + ",\n\n"
                    + "Automated status update regarding your jewellery restoration:\n\n"
                    + "• Docket Reference: #REP-" + String.format("%04d", repair.getId()) + "\n"
                    + "• Item: " + repair.getItemName() + "\n"
                    + "• Current Phase: " + status.toUpperCase() + "\n"
                    + "• Date Handed Over: " + (repair.getReceivedDate() != null ? repair.getReceivedDate() : "Recorded at bench") + "\n"
                    + "• Target Completion: " + (repair.getReturnDate() != null ? repair.getReturnDate() : "Scheduled") + "\n"
                    + "• Service Charge: LKR " + String.format("%,.2f", repair.getEstimatedCost() != null ? repair.getEstimatedCost() : 0.0) + "\n\n"
                    + statusMessage + "\n\n"
                    + "Thank you for trusting AURUM JEWELS Atelier.\n"
                    + "48 Galle Face Court, Colombo 03 | +94 11 234 5678";

            notificationService.notifyCustomer(userId, recipientEmail, "AURUM JEWELS | Repair Docket Milestone: " + status, emailBody);
        }
    }

    @Override
    @Transactional
    public void notifyRepairReady(Integer id, String customEmail) {
        Repair repair = repairRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repair docket not found with ID: " + id));

        Customer cust = repair.getCustomer();
        if (cust == null) {
            throw new RuntimeException("No patron associated with Repair Docket #REP-" + String.format("%04d", id));
        }

        // If custom email is passed and not blank, update customer record
        if (customEmail != null && !customEmail.isBlank()) {
            cust.setEmail(customEmail.trim());
            customerRepository.save(cust);
        }

        String recipientEmail = cust.getEmail();
        if (recipientEmail == null || recipientEmail.isBlank()) {
            throw new RuntimeException("Patron has no email address on record for Repair #REP-" + String.format("%04d", id));
        }

        String patronName = (cust.getName() != null && !cust.getName().isBlank()) ? cust.getName() : "Valued Patron";
        Integer userId = (cust.getUser() != null) ? cust.getUser().getId() : null;

        String subject = "AURUM JEWELS | Your Restored Piece is Ready for Collection (#REP-" + String.format("%04d", repair.getId()) + ")";

        String emailBody = "Dear " + patronName + ",\n\n"
                + "We are pleased to inform you that your fine jewellery restoration has been completed with the utmost precision.\n\n"
                + "Your piece has passed our master artisan quality assay and is now safely sealed in our vault, READY for collection at our Colombo Flagship Boutique.\n\n"
                + "Docket Summary:\n"
                + "• Docket Reference: #REP-" + String.format("%04d", repair.getId()) + "\n"
                + "• Jewellery Piece: " + repair.getItemName() + "\n"
                + "• Scope of Work: " + (repair.getDescription() != null ? repair.getDescription() : "Atelier Fine Restoration") + "\n"
                + "• Date Handed Over: " + (repair.getReceivedDate() != null ? repair.getReceivedDate() : "Recorded at intake") + "\n"
                + "• Total Service Fee: LKR " + String.format("%,.2f", repair.getEstimatedCost() != null ? repair.getEstimatedCost() : 0.0) + "\n\n"
                + "Collection Boutique:\n"
                + "AURUM JEWELS Atelier & Private Vault\n"
                + "48 Galle Face Court, Colombo 03 | +94 11 234 5678\n"
                + "Opening Hours: Monday – Saturday, 10:00 AM – 7:00 PM\n\n"
                + "Please present this docket reference upon collection.\n\n"
                + "Thank you for trusting AURUM JEWELS Atelier.\n"
                + "48 Galle Face Court, Colombo 03 | +94 11 234 5678";

        notificationService.notifyCustomer(userId, recipientEmail.trim(), subject, emailBody);
    }

    @Override
    public List<RepairDTO> getAllRepairs() {
        return repairRepository.findAll().stream()
                .map(r -> RepairDTO.builder()
                        .id(r.getId())
                        .itemName(r.getItemName())
                        .description(r.getDescription())
                        .status(r.getStatus())
                        .receivedDate(r.getReceivedDate())
                        .returnDate(r.getReturnDate())
                        .estimatedCost(r.getEstimatedCost())
                        .customerId(r.getCustomer() != null ? r.getCustomer().getId() : null)
                        .customerName(r.getCustomer() != null ? r.getCustomer().getName() : "Walk-in Patron")
                        .customerContact(r.getCustomer() != null ? r.getCustomer().getContact() : "")
                        .customerEmail(r.getCustomer() != null ? r.getCustomer().getEmail() : "")
                        .customerAddress(r.getCustomer() != null ? r.getCustomer().getAddress() : "")
                        .build())
                .toList();
    }

    @Override
    public RepairStatsDTO getRepairStats() {
        List<Repair> allRepairs = repairRepository.findAll();
        long totalRepairs = allRepairs.size();
        long readyCount = allRepairs.stream().filter(r -> "Ready".equalsIgnoreCase(r.getStatus())).count();
        long craftingCount = allRepairs.stream()
                .filter(r -> "Crafting".equalsIgnoreCase(r.getStatus()) || "Melting".equalsIgnoreCase(r.getStatus()))
                .count();
        double totalRevenue = allRepairs.stream()
                .mapToDouble(r -> r.getEstimatedCost() != null ? r.getEstimatedCost() : 0.0)
                .sum();
        return new RepairStatsDTO(totalRepairs, readyCount, craftingCount, totalRevenue);
    }
}