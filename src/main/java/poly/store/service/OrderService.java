package poly.store.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import poly.store.entity.Order;
import poly.store.entity.Book;
import poly.store.model.BestSellerModel;
import poly.store.model.DetailOrder;
import poly.store.model.OrderModel;
import poly.store.model.StatisticalBookDay;
import poly.store.model.StatisticalRevenue;
import poly.store.model.StatisticalTotalOrder;

public interface OrderService {

	List<Order> getOrderByName(String code);

	void save(Order order);
	
	void delete(Order order);

	List<OrderModel> listOrderHistory();

	List<Order> listOrderByCodeAndUsername(String id);

	List<OrderModel> listOrderGroupByCode();

	DetailOrder getDetailOrderByCode(String id);

	void approveOrder(String id);

	void cancelOrder(String id);

	List<OrderModel> listOrderGroupByCodeShipping();

	void shippedOrder(String id);

	List<OrderModel> listOrderGroupByCodeSuccess();

	List<OrderModel> listOrderGroupByCodeCancel();

	void deleteOrder(String id);

	List<StatisticalBookDay> listStatisticalBookDay();
	
	List<StatisticalRevenue> listStatisticalRevenueDay(int day, int month, int year);

	List<StatisticalRevenue> listStatisticalRevenueByMonth(int month, int year);

	List<StatisticalRevenue> listStatisticalRevenueByYear(int year);

	StatisticalTotalOrder getStatisticalTotalOrderOnDay(int day, int month, int year);

	StatisticalTotalOrder getStatisticalTotalOrderOnMonth(int month, int year);

	StatisticalTotalOrder getStatisticalTotalOrderOnYear(int year);

	List<Integer> getListYearOrder();
	
	List<StatisticalRevenue> listStatisticalTotalRevenueOnOption(int day, int month, int year);

	StatisticalTotalOrder getStatisticalTotalOrderOnOption(int day, int month, int year);

	List<BestSellerModel> getListBestSellerBook(Pageable topFour);

	List<Book> listStatisticalBookWarehouse();
	
	// Thêm phương thức đếm số lượng đơn hàng theo trạng thái
	int countPendingOrders();

	int countShippingOrders();

	int countSuccessOrders();

	int countCancelOrders();
	
	// ✅ Thêm phương thức tìm đơn hàng theo mã code
	List<Order> getOrderByCode(String code);
	
	

}
