package poly.store.rest.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import poly.store.service.BookReviewsService;
import poly.store.service.ContactService;
import poly.store.service.OrderService;

@RestController
@RequestMapping("/rest/count")
public class CountRestController {
    
    @Autowired
    private OrderService orderService;

    @Autowired
    private BookReviewsService commentService;

    @Autowired
    private ContactService contactService;

    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getNotificationCount() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("pendingOrders", orderService.countPendingOrders());
        counts.put("shippingOrders", orderService.countShippingOrders());
        counts.put("successOrders", orderService.countSuccessOrders());
        counts.put("cancelOrders", orderService.countCancelOrders());
        
        counts.put("pendingBookReviews", commentService.countPendingBookReviews());
        counts.put("successBookReviews", commentService.countSuccessBookReviews());
        
        counts.put("pendingContacts", contactService.countPendingContacts());
        counts.put("successContacts", contactService.countSuccessContacts());
        return ResponseEntity.ok(counts);
    }
}
