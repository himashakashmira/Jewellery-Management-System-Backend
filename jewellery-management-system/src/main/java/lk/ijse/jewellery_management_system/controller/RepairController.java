package lk.ijse.jewellery_management_system.controller;

import lk.ijse.jewellery_management_system.dto.RepairDTO;
import lk.ijse.jewellery_management_system.service.JewelleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/repairs")
@RequiredArgsConstructor
public class RepairController {

    private final JewelleryService jewelleryService;

    @PostMapping("/register")
    public String register(@RequestBody RepairDTO dto) {
        jewelleryService.registerRepair(dto);
        return "Repair registered successfully!";
    }

    @PatchMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Integer id, @RequestParam String status) {
        jewelleryService.updateRepairStatus(id, status);
        return "Status updated to " + status;
    }

    @GetMapping("/all")
    public List<RepairDTO> getAll() {
        return jewelleryService.getAllRepairs();
    }
}