package poly.store.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import javax.servlet.http.HttpSession;

import poly.store.service.impl.ShippingServiceImpl;

@RestController
@RequestMapping("/rest")
public class ShippingRestController {

    @Autowired
    private ShippingServiceImpl shippingService;

    @PostMapping("/shipping-fee")
    public ResponseEntity<Map<String, Integer>> calculateShippingFee(@RequestBody Map<String, Object> requestData, HttpSession session) {
        try {
            System.out.println("Dữ liệu nhận được: " + requestData);  // Log để kiểm tra request

            if (!requestData.containsKey("toDistrictId") || 
                !requestData.containsKey("toWardCode") || 
                !requestData.containsKey("weight")) {
                return ResponseEntity.badRequest().body(Map.of("error", -1)); // Lỗi thiếu dữ liệu
            }

            int toDistrictId = Integer.parseInt(requestData.get("toDistrictId").toString());
            String toWardCode = requestData.get("toWardCode").toString();
            int weight = Integer.parseInt(requestData.get("weight").toString());

            System.out.println("toDistrictId: " + toDistrictId + ", toWardCode: " + toWardCode + ", weight: " + weight);

            int fee = shippingService.getShippingFee(toDistrictId, toWardCode, weight);
            
            System.out.println("Phí ship tính được: " + fee);
            // Lưu phí ship vào session
            session.setAttribute("shippingFee", fee);
            return ResponseEntity.ok(Map.of("fee", fee));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", -2));
        }
    }

}
