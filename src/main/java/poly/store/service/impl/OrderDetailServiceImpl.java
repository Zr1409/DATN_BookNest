package poly.store.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.store.entity.OrderDetail;
import poly.store.dao.OrderDetailDao;
import poly.store.service.OrderDetailService;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {
    @Autowired
    private OrderDetailDao orderDetailDao;

    @Override
    public void save(OrderDetail orderDetail) {
        orderDetailDao.save(orderDetail);
    }
    
}
