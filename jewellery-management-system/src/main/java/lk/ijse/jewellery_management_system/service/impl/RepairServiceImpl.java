package lk.ijse.jewellery_management_system.service.impl;

import lk.ijse.jewellery_management_system.dto.RepairDTO;
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
        Customer customer = customerRepository.findById(dto.getCustomerId()).orElseThrow();
        Repair repair = Repair.builder()
                .itemName(dto.getItemName())
                .description(dto.getDescription())
                .status("Received")
                .receivedDate(LocalDate.now())
                .estimatedCost(dto.getEstimatedCost())
                .customer(customer)
                .build();
        repairRepository.save(repair);
    }

    @Override
    @Transactional
    public void updateRepairStatus(Integer id, String status) {
        Repair repair = repairRepository.findById(id).orElseThrow();
        repair.setStatus(status);
        repairRepository.save(repair);

        // check if repair is finished to send notification
        if (status.equalsIgnoreCase("Ready")) {
            String message = "Your jewellery repair (" + repair.getItemName() + ") is now ready for pickup!";

            // trigger notification and email
            notificationService.notifyCustomer(
                    repair.getCustomer().getUser().getId(),
                    repair.getCustomer().getEmail(),
                    message
            );
        }
    }

    @Override
    public List<RepairDTO> getAllRepairs() {
        return repairRepository.findAll().stream()
                .map(r -> new RepairDTO(
                        r.getId(),
                        r.getItemName(),
                        r.getDescription(),
                        r.getStatus(),
                        r.getReceivedDate(),
                        r.getEstimatedCost(),
                        r.getCustomer().getId()))
                .toList();
    }
}