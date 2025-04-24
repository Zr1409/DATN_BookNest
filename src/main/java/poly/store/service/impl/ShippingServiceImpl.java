package poly.store.service.impl;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class ShippingServiceImpl {

    private static final String GHN_API_URL = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";
    private static final String TOKEN = "17c8d9c0-04c1-11f0-aff4-822fc4284d92"; // Thay bằng Token thật của bạn

    private final int SHOP_ID = 1442;

    public Integer getShippingFee(int toDistrictId, String toWardCode, int weight) {
        RestTemplate restTemplate = new RestTemplate();
        
        // Tạo request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("from_district_id", SHOP_ID);
        requestBody.put("to_district_id", toDistrictId);
        requestBody.put("to_ward_code", toWardCode);
        requestBody.put("weight", weight);
        requestBody.put("length", 10);
        requestBody.put("width", 10);
        requestBody.put("height", 10);
        requestBody.put("service_id", 53321); // Sửa đúng service_id
        requestBody.put("service_type_id", 2);

     // Cấu hình headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Token", TOKEN);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(GHN_API_URL, HttpMethod.POST, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                return Integer.parseInt(data.get("total").toString());
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi gọi API GHN: " + e.getMessage());
        }
        
        return 0;
    }
}
