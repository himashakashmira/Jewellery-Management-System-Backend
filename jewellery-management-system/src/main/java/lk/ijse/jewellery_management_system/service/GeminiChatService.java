package lk.ijse.jewellery_management_system.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lk.ijse.jewellery_management_system.dto.GoldRateDTO;
import lk.ijse.jewellery_management_system.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GeminiChatService {

    @Value("${gemini.api.key:AIzaSyDRzx3u5VqUBoFn8QiC7a91lSXuHtBOj2g}")
    private String apiKey;

    private final GoldRateService goldRateService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final RepairService repairServicee;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateReply(String userMessage) {
        // Google සේවාදායකයේ තාවකාලික 503 තදබදයන් මඟහැරීමට වාර 3ක් Retry කරයි
        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                // 1. Fetch live system context from database
                GoldRateDTO latestRate = goldRateService.getLatestRate();
                List<ProductDTO> products = productService.getAllProducts();

                String storeContext = String.format(
                        "Current Gold Rates details: %s. Available Store Products Count: %d.",
                        latestRate != null ? latestRate.toString() : "N/A",
                        products != null ? products.size() : 0
                );

                // 2. Build system instructions
                String systemInstructions = """
                    You are 'Aurum AI', the intelligent luxury jewellery consultant and customer assistant for 'Aurum Jewellery'.
                    
                    STORE LIVE DATA CONTEXT:
                    """ + storeContext + """
                    
                    KNOWLEDGE & BEHAVIOR RULES:
                    1. GENERAL JEWELLERY EXPERTISE:
                       - You CAN and MUST answer ANY question related to jewellery, gemstones (diamonds, sapphires, rubies), precious metals (gold, silver, platinum), gold karats (24K, 22K, 18K), jewellery cleaning/maintenance advice, styling tips, diamond cuts, and hallmarking, EVEN IF it is not in our local database.
                    
                    2. STORE INQUIRIES:
                       - Use the live store context above to answer current gold rates or general product inquiries accurately.
                    
                    3. STRICT NON-JEWELLERY RESTRICTION:
                       - If the user asks about anything completely unrelated to jewellery (e.g. sports, politics, programming, movies, cooking recipes, weather, medical advice), politely refuse:
                         "I am a specialized AI consultant dedicated exclusively to jewellery, precious metals, and Aurum Jewellery store services. I cannot assist with non-jewellery topics."
                         (If asked in Sinhala, politely respond in Sinhala: "සමාවෙන්න, මට සහාය විය හැක්කේ රන් ආභරණ, රන් මිල සහ Aurum Jewellery ආයතනයට අදාළ තොරතුරු පිළිබඳව පමණි.").
                    
                    4. TONE & LANGUAGE:
                       - Respond politely and professionally in the language of the user (Sinhala, English, or Singlish).
                    """;

                // API URL: Google විසින් නියම කර ඇති නිවැරදි 'gemini-3.6-flash' මාදිලිය
                String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=" + apiKey;

                Map<String, Object> textPart = Map.of("text", systemInstructions + "\n\nUser Question: " + userMessage);
                Map<String, Object> contentPart = Map.of("parts", List.of(textPart));
                Map<String, Object> requestBody = Map.of("contents", List.of(contentPart));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

                // Parse response
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

            } catch (Exception e) {
                if (attempt == maxRetries) {
                    return "සමාවෙන්න, සේවාදායකය සමඟ සම්බන්ධ වීමේදී ගැටලුවක් මතු විය: " + e.getMessage();
                }
                try {
//                    delay time
                    Thread.sleep(1500);
                } catch (InterruptedException ignored) {}
            }
        }
        return "කරුණාකර නැවත උත්සාහ කරන්න.";
    }
}
