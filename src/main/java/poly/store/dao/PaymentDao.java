package poly.store.dao;


import org.springframework.data.jpa.repository.JpaRepository;

import poly.store.entity.Order;
import poly.store.entity.Payment;

public interface PaymentDao extends JpaRepository<Payment, Integer> {
	Payment findByOrder(Order order);


}
