package lk.ijse.jewellery_management_system.service;

import lk.ijse.jewellery_management_system.dto.RepairDTO;

import java.util.List;

public interface RepairService {
    void registerRepair(RepairDTO dto);

    void updateRepairStatus(Integer id, String status);

    List<RepairDTO> getAllRepairs();
}