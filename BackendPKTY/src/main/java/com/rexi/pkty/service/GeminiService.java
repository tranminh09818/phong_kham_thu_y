package com.rexi.pkty.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rexi.pkty.dto.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String modelName;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SYSTEM_PROMPT =
            "Bạn là Bác sĩ Thú y AI của Phòng khám Rexi. Tuân thủ NGHIÊM NGẶT:\n" +
                    "1. Phong cách: Thấu cảm, chuyên môn cao, ngắn gọn (tối đa 4 câu). Xưng 'Bạn', gọi thú cưng là 'Bé'.\n" +
                    "2. XỬ LÝ TEXT KHÔNG DẤU: Khách hay viết sai chính tả hoặc không dấu (vd: 'ia'=ỉa/đi ngoài, 'tieu chay'=tiêu chảy, 'nah to'=nhà tao, 'ngua'=ngứa). BẮT BUỘC đọc kỹ ngữ cảnh để chẩn đoán đúng bệnh (tiêu hóa, da liễu, hô hấp...). KHÔNG ĐƯỢC tự động tư vấn về 'ngứa' hay 'da liễu' nếu khách không hề nhắc đến.\n" +
                    "3. PHÂN TÍCH ẢNH/VIDEO: Đóng vai bác sĩ khám lâm sàng. Nhận xét cụ thể về: màu da/lông, tình trạng mắt/tai, vết thương, tư thế cơ thể. Phân loại mức độ: Bình thường / Cần theo dõi / Cần khám ngay.\n" +
                    "4. TƯ VẤN BỆNH: Luôn liên hệ triệu chứng đúng với bệnh thú cưng. Nếu là vấn đề tiêu hóa (ỉa chảy, nôn), phải tư vấn về đường ruột.\n" +
                    "5. [LINK ĐẶT LỊCH]: CHỈ thêm khi (a) Khách hỏi đặt lịch; (b) Triệu chứng cần can thiệp (u cục, vết thương hở, tiêu chảy nặng). KHÔNG thêm cho tư vấn dinh dưỡng hoặc ngứa nhẹ.\n" +
                    "6. [LINK BẢN ĐỒ]: BẮT BUỘC thêm khi (a) Khách hỏi địa chỉ, vị trí phòng khám; (b) Tình trạng nguy kịch. Ví dụ: 'Bạn xem vị trí tại đây: [LINK BẢN ĐỒ]'.\n" +
                    "7. KHẨN CẤP (viết hoa): BẮT BUỘC dùng từ 'KHẨN CẤP' khi thấy dấu hiệu nguy kịch (khó thở, co giật, máu...). Luôn đi kèm [LINK BẢN ĐỒ].\n" +
                    "8. Từ chối nhẹ nhàng các chủ đề không liên quan thú cưng. Không dùng 'ba mẹ'.";

    public String chat(List<ChatMessage> history) throws Exception {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;

        Map<String, Object> requestBodyMap = new HashMap<>();

        // 1. Set System Instruction
        Map<String, Object> systemInstruction = new HashMap<>();
        systemInstruction.put("parts", Map.of("text", SYSTEM_PROMPT));
        requestBodyMap.put("system_instruction", systemInstruction);

        // 2. Set Contents
        List<Map<String, Object>> contents = new ArrayList<>();

        for (ChatMessage msg : history) {
            Map<String, Object> contentItem = new HashMap<>();
            
            // Chuyển role sang chuẩn của Gemini (user / model)
            String role = (msg.getRole() != null && msg.getRole().equals("assistant")) ? "model" : "user";
            contentItem.put("role", role);

            List<Map<String, Object>> parts = new ArrayList<>();

            String textContent = (msg.getContent() != null && !msg.getContent().isBlank()) ? msg.getContent() : "";

            if (msg.getImage() != null && !msg.getImage().isEmpty()) {
                if (textContent.isBlank()) textContent = "Phân tích ảnh này giúp tôi.";
                parts.add(Map.of("text", textContent));
                parts.add(Map.of("inlineData", Map.of(
                        "mimeType", "image/jpeg",
                        "data", msg.getImage()
                )));
            } else if (msg.getVideo() != null && !msg.getVideo().isEmpty()) {
                if (textContent.isBlank()) textContent = "Phân tích video này giúp tôi.";
                parts.add(Map.of("text", textContent));
                
                String mimeType = "video/mp4";
                String base64Data = msg.getVideo();
                
                // Trích xuất chính xác mimeType từ chuỗi data URL
                if (base64Data.startsWith("data:")) {
                    int semicolonIdx = base64Data.indexOf(";");
                    if (semicolonIdx != -1) {
                        mimeType = base64Data.substring(5, semicolonIdx);
                    }
                    int commaIdx = base64Data.indexOf(",");
                    if (commaIdx != -1) {
                        base64Data = base64Data.substring(commaIdx + 1);
                    }
                }
                
                parts.add(Map.of("inlineData", Map.of(
                        "mimeType", mimeType,
                        "data", base64Data
                )));
            } else {
                if (!textContent.isBlank()) {
                    parts.add(Map.of("text", textContent));
                }
            }

            if (!parts.isEmpty()) {
                contentItem.put("parts", parts);
                contents.add(contentItem);
            }
        }

        requestBodyMap.put("contents", contents);

        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(60)) // Gemini xử lý video có thể lâu hơn
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.err.println("=== GEMINI API ERROR ===");
            System.err.println("Status: " + response.statusCode());
            System.err.println("Body: " + response.body());
            throw new RuntimeException("Gemini API Error " + response.statusCode() + ": " + response.body());
        }

        JsonNode rootNode = objectMapper.readTree(response.body());
        try {
            return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        } catch (Exception e) {
            System.err.println("Gemini Parsing Error. Response: " + response.body());
            throw new RuntimeException("Gemini Parse Error: " + response.body());
        }
    }
}
