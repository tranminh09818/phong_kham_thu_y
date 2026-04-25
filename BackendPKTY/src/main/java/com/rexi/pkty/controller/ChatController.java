package com.rexi.pkty.controller;

import com.rexi.pkty.dto.ChatMessage;
import com.rexi.pkty.service.GroqService;
import com.rexi.pkty.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private GroqService groqService;

    @Autowired
    private GeminiService geminiService;

    @PostMapping
    public Map<String, String> chat(@RequestBody List<ChatMessage> history) {
        try {
            if (history == null || history.isEmpty()) {
                return Map.of("reply", "Xin chào! Tôi có thể giúp gì cho bé?");
            }

            ChatMessage latest = history.get(history.size() - 1);
            boolean hasImage = latest.getImage() != null && !latest.getImage().isEmpty();
            boolean hasVideo = latest.getVideo() != null && !latest.getVideo().isEmpty();

            // Routing Logic
            if (hasVideo || hasImage) {
                // Ưu tiên dùng Gemini cho Multi-modal (Đặc biệt là Video)
                String reply = geminiService.chat(history);
                return Map.of("reply", reply);
            } else {
                // Ưu tiên dùng Groq cho Text vì tốc độ cực nhanh
                try {
                    String reply = groqService.chat(history);
                    return Map.of("reply", reply);
                } catch (Exception groqException) {
                    System.err.println("Groq failed, falling back to Gemini... Error: " + groqException.getMessage());
                    // Fallback sang Gemini
                    String fallbackReply = geminiService.chat(history);
                    return Map.of("reply", fallbackReply);
                }
            }
        } catch (Exception e) {
            System.err.println("Cả hai AI đều lỗi: " + e.getMessage());
            return Map.of("reply", "Lỗi: " + e.getMessage());
        }
    }
}
