package poly.store.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.store.dao.PaymentDao;
import poly.store.entity.Order;
import poly.store.entity.Payment;
import poly.store.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {
	@Autowired
	PaymentDao paymentDao;
	
	@Override
	public Payment findByOrder(Order order){
		return paymentDao.findByOrder(order);
	}
	@Override
	public void save(Payment payment) {
		paymentDao.save(payment);
	}
}
