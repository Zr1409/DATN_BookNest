package poly.store.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import poly.store.service.ChatGPTService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rest/chatbox")
public class ChatbotRestController {
    @Autowired
    private ChatGPTService chatGPTService;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Map<String, String>> chat(
            @RequestParam("message") String message,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            // Gọi AI xử lý tin nhắn và ảnh (nếu có)
            String aiResponse = chatGPTService.getChatResponse(message, image);

            Map<String, String> response = new HashMap<>();
            response.put("reply", aiResponse);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("reply", "Lỗi khi xử lý yêu cầu, xin thử lại.");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

}
