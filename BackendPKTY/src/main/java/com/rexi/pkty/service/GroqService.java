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
import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    @Value("${groq.model:llama-3.3-70b-versatile}")
    private String modelName;

    // Model để phân tích hình ảnh
    @Value("${groq.vision.model:meta-llama/llama-4-scout-17b-16e-instruct}")
    private String visionModelName;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SYSTEM_PROMPT =
            "Bạn là Bác sĩ Thú y AI của Phòng khám Rexi. Tuân thủ NGHIÊM NGẶT:\n" +
                    "1. Phong cách: Thấu cảm, chuyên môn cao, ngắn gọn (tối đa 4 câu). Xưng 'Bạn', gọi thú cưng là 'Bé'.\n" +
                    "2. XỬ LÝ TEXT KHÔNG DẤU: Khách hay viết sai chính tả hoặc không dấu (vd: 'ia'=ỉa/đi ngoài, 'tieu chay'=tiêu chảy, 'nah to'=nhà tao, 'ngua'=ngứa). BẮT BUỘC đọc kỹ ngữ cảnh để chẩn đoán đúng bệnh (tiêu hóa, da liễu, hô hấp...). KHÔNG ĐƯỢC tự động tư vấn về 'ngứa' hay 'da liễu' nếu khách không hề nhắc đến.\n" +
                    "3. PHÂN TÍCH ẢNH: Đóng vai bác sĩ khám lâm sàng. Nhận xét cụ thể về: màu da/lông, tình trạng mắt/tai, vết thương, tư thế cơ thể. Phân loại mức độ: Bình thường / Cần theo dõi / Cần khám ngay. Nếu ảnh mờ/tối, yêu cầu chụp lại.\n" +
                    "4. TƯ VẤN BỆNH: Luôn liên hệ triệu chứng đúng với bệnh thú cưng. Nếu là vấn đề tiêu hóa (ỉa chảy, nôn), phải tư vấn về đường ruột.\n" +
                    "5. [LINK ĐẶT LỊCH]: CHỈ thêm khi (a) Khách hỏi đặt lịch; (b) Triệu chứng cần can thiệp (u cục, vết thương hở, tiêu chảy nặng). KHÔNG thêm cho tư vấn dinh dưỡng hoặc ngứa nhẹ.\n" +
                    "6. [LINK BẢN ĐỒ]: BẮT BUỘC thêm khi (a) Khách hỏi địa chỉ, vị trí phòng khám; (b) Tình trạng nguy kịch. Ví dụ: 'Bạn xem vị trí tại đây: [LINK BẢN ĐỒ]'.\n" +
                    "7. KHẨN CẤP (viết hoa): BẮT BUỘC dùng từ 'KHẨN CẤP' khi thấy dấu hiệu nguy kịch (khó thở, co giật, máu...). Luôn đi kèm [LINK BẢN ĐỒ].\n" +
                    "8. Từ chối nhẹ nhàng các chủ đề không liên quan thú cưng. Không dùng 'ba mẹ'.";

    public String chat(List<ChatMessage> history) throws Exception {
        ChatMessage latest = history.get(history.size() - 1);
        String latestContent = latest.getContent() != null ? latest.getContent() : "";
        String latestNormalized = normalizeVietnamese(latestContent.toLowerCase());

        // Hard Filter từ khóa khẩn cấp (Life-threatening symptoms)
        if (latestNormalized.contains("co giat") || latestNormalized.contains("kho tho") ||
                latestNormalized.contains("chay mau") || latestNormalized.contains("ngo doc") ||
                latestNormalized.contains("tai nan") || latestNormalized.contains("nuot phai") ||
                latestNormalized.contains("hon me") || latestNormalized.contains("bat tinh") ||
                (latestNormalized.contains("loi") && (latestNormalized.contains("ruot") || latestNormalized.contains("mat") || latestNormalized.contains("xuong"))) ||
                latestNormalized.contains("hap hoi") || latestNormalized.contains("ngat") ||
                latestNormalized.contains("tim tai") || latestNormalized.contains("sap chet") ||
                latestNormalized.contains("gay xuong") || latestNormalized.contains("khong cu dong")) {
            return "TÌNH TRẠNG CỦA BÉ RẤT KHẨN CẤP! Đây là tình huống đe dọa tính mạng. Bạn hãy ngừng chat và đưa bé đến ngay Phòng khám thú y Rexi để được hỗ trợ cấp cứu kịp thời nhé! [LINK BẢN ĐỒ]";
        }

        // Hard Filter cho địa chỉ/vị trí
        if (latestNormalized.contains("dia chi") || latestNormalized.contains("o dau") || 
            latestNormalized.contains("vi tri") || latestNormalized.contains("duong di")) {
            return "Phòng khám Rexi tọa lạc tại: Số 68, Ngõ 10, Đường Ngô Xuân Quảng, Trâu Quỳ, Gia Lâm, Hà Nội. Bạn có thể xem chỉ đường chi tiết tại đây: [LINK BẢN ĐỒ]";
        }

        // Kiểm tra ảnh → chọn model phù hợp
        boolean hasImage = latest.getImage() != null && !latest.getImage().isEmpty();
        String selectedModel = hasImage ? visionModelName : modelName;

        // Chuẩn bị danh sách messages cho API
        List<Map<String, Object>> messagesForApi = new ArrayList<>();
        messagesForApi.add(Map.of("role", "system", "content", SYSTEM_PROMPT));

        for (int i = 0; i < history.size(); i++) {
            ChatMessage msg = history.get(i);
            String msgContent = msg.getContent() != null && !msg.getContent().isBlank() ? msg.getContent() : "";
            
            boolean isLatest = (i == history.size() - 1);
            
            if (isLatest && msg.getImage() != null && !msg.getImage().isEmpty()) {
                String textForImage = msgContent.isBlank() ? "Phân tích ảnh này và nhận định sức khỏe của bé." : msgContent;
                List<Map<String, Object>> content = new ArrayList<>();
                content.add(Map.of("type", "text", "text", textForImage));
                content.add(Map.of(
                    "type", "image_url",
                    "image_url", Map.of("url", "data:image/jpeg;base64," + msg.getImage())
                ));
                messagesForApi.add(Map.of("role", msg.getRole(), "content", content));
            } else if (!msgContent.isBlank()) {
                messagesForApi.add(Map.of("role", msg.getRole(), "content", msgContent));
            }
        }

        // Dùng model phù hợp: vision cho ảnh, text cho chat thường
        Map<String, Object> requestBodyMap = Map.of(
                "model", selectedModel,
                "messages", messagesForApi
        );

        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GROQ_API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Xử lý lỗi API - log chi tiết để debug
        if (response.statusCode() != 200) {
            System.err.println("=== GROQ API ERROR ===");
            System.err.println("Model: " + selectedModel);
            System.err.println("Status: " + response.statusCode());
            System.err.println("Body: " + response.body());
            
            // Trả về thông tin lỗi cụ thể cho frontend (chỉ dùng khi debug)
            return "Hệ thống AI gặp lỗi (Status " + response.statusCode() + "). Chi tiết: " + response.body();
        }

        JsonNode rootNode = objectMapper.readTree(response.body());
        try {
            return rootNode.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            return "Tôi rất lo cho bé nhưng hệ thống đang trục trặc. Bạn hãy đưa bé đến Rexi sớm để bác sĩ kiểm tra cho yên tâm nhé!";
        }
    }

    /**
     * Bỏ dấu tiếng Việt để so sánh từ khóa khẩn cấp,
     * giúp nhận diện khi user gõ không dấu.
     */
    private String normalizeVietnamese(String input) {
        String result = input
            .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
            .replaceAll("[èéẹẻẽêềếệểễ]", "e")
            .replaceAll("[ìíịỉĩ]", "i")
            .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
            .replaceAll("[ùúụủũưừứựửữ]", "u")
            .replaceAll("[ỳýỵỷỹ]", "y")
            .replaceAll("[đ]", "d")
            .replaceAll("[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]", "a")
            .replaceAll("[ÈÉẸẺẼÊỀẾỆỂỄ]", "e")
            .replaceAll("[ÌÍỊỈĨ]", "i")
            .replaceAll("[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]", "o")
            .replaceAll("[ÙÚỤỦŨƯỪỨỰỬỮ]", "u")
            .replaceAll("[ỲÝỴỶỸ]", "y")
            .replaceAll("[Đ]", "d");
        return result;
    }
}
