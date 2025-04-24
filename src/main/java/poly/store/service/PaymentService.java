package poly.store.service;

import poly.store.entity.Order;
import poly.store.entity.Payment;

public interface PaymentService {
	void save(Payment payment);
	
	Payment findByOrder(Order order);
	           
}
