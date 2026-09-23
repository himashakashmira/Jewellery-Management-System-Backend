package lk.ijse.jewellery_management_system.controller;

import lk.ijse.jewellery_management_system.service.GeminiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatBotController {

    private final GeminiChatService geminiChatService;

    @PostMapping("/ask")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, String>> chatWithBot(@RequestBody Map<String, String> payload) {
        String userQuery = payload.get("message");

        if (userQuery == null || userQuery.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("reply", "කරුණාකර වලංගු ප්‍රශ්නයක් ඇතුළත් කරන්න."));
        }

        String reply = geminiChatService.generateReply(userQuery);
        return ResponseEntity.ok(Map.of("reply", reply));
    }
}
