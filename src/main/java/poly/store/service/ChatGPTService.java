package poly.store.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import java.util.*;

import java.util.Base64;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ChatGPTService {
	//curl -X GET "https://generativelanguage.googleapis.com/v1/models?key=AIzaSyAWwbKs-_pufbvVy43AtJIbl9CtPbX6QRY"
	//AIzaSyAWwbKs-_pufbvVy43AtJIbl9CtPbX6QRY
	private String apiKey = "AIzaSyB_sj6nDRH_ABDOdd7vifcirE5p4vxpNjU";
	private final RestTemplate restTemplate = new RestTemplate();
	private static final String API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-pro-002:generateContent?key=";
	private final ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	BookService bookService;

	public String getChatResponse(String userMessage, MultipartFile imageFile) {
	    try {
	        // Chuẩn bị request body
	        Map<String, Object> textPart = new HashMap<>();
	        textPart.put("text", userMessage);

	        List<Map<String, Object>> parts = new ArrayList<>();
	        parts.add(textPart); // Luôn có văn bản

	        // 🌟 Nếu có ảnh, chuyển ảnh thành base64
	        if (imageFile != null && !imageFile.isEmpty()) {
	            byte[] imageBytes = imageFile.getBytes();
	            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

	            Map<String, Object> imagePart = new HashMap<>();
	            imagePart.put("inlineData", Map.of(
	                "mimeType", imageFile.getContentType(),
	                "data", base64Image
	            ));
	            parts.add(imagePart);
	        }

	        Map<String, Object> content = new HashMap<>();
	        content.put("parts", parts);

	        Map<String, Object> requestBodyMap = new HashMap<>();
	        requestBodyMap.put("contents", Collections.singletonList(content));

	        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

	        // Gửi request
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

	        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
	        ResponseEntity<Map> response = restTemplate.exchange(API_URL + apiKey, HttpMethod.POST, request, Map.class);

	        // Xử lý phản hồi từ Gemini
	        if (response.getBody() != null && response.getBody().containsKey("candidates")) {
	            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
	            if (!candidates.isEmpty()) {
	                Map<String, Object> firstCandidate = candidates.get(0);
	                if (firstCandidate.containsKey("content")) {
	                    Map<String, Object> contentMap = (Map<String, Object>) firstCandidate.get("content");
	                    if (contentMap.containsKey("parts")) {
	                        List<Map<String, Object>> responseParts = (List<Map<String, Object>>) contentMap.get("parts");

	                        StringBuilder responseText = new StringBuilder();
	                        for (Map<String, Object> part : responseParts) {
	                            if (part.containsKey("text")) {
	                                responseText.append(part.get("text").toString()).append("\n");
	                            }
	                        }
	                        return responseText.toString();
	                    }
	                }
	            }
	        }

	        return "Không có phản hồi từ AI.";
	    } catch (Exception e) {
	        return "Lỗi khi gửi yêu cầu: " + e.getMessage();
	    }
	}

}