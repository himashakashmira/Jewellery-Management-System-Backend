package lk.ijse.jewellery_management_system.controller;

import lk.ijse.jewellery_management_system.dto.RepairDTO;
import lk.ijse.jewellery_management_system.dto.RepairStatsDTO;
import lk.ijse.jewellery_management_system.service.RepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/repairs")
@RequiredArgsConstructor
public class RepairController {

    private final RepairService repairServicee;

    @PostMapping("/register")
    public String register(@RequestBody RepairDTO dto) {
        repairServicee.registerRepair(dto);
        return "Repair registered successfully!";
    }

    @PatchMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Integer id, @RequestParam String status) {
        repairServicee.updateRepairStatus(id, status);
        return "Status updated to " + status;
    }

    @PostMapping(value = {"/notify/{id}", "/{id}/notify"})
    public ResponseEntity<?> notifyCustomer(@PathVariable Integer id, @RequestParam(required = false) String email) {
        try {
            repairServicee.notifyRepairReady(id, email);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Collection notification email successfully dispatched to patron!"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/all")
    public List<RepairDTO> getAll() {
        return repairServicee.getAllRepairs();
    }

    @GetMapping("/stats")
    public RepairStatsDTO getStats() {
        return repairServicee.getRepairStats();
    }
}