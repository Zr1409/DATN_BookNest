package poly.store.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import poly.store.entity.OrderDetail;

public interface OrderDetailDao extends JpaRepository<OrderDetail, Integer> {
	 @Query("SELECT od FROM OrderDetail od WHERE od.order.code = :code")
	    List<OrderDetail> getOrderDetailByCode(@Param("code") String code);
}
