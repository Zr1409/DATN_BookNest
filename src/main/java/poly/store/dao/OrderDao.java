package poly.store.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import poly.store.entity.Order;
import poly.store.model.BestSellerModel;
import poly.store.model.OrderModel;
import poly.store.model.StatisticalOrder;
import poly.store.model.StatisticalBookDay;

public interface OrderDao extends JpaRepository<Order, Integer> {
	@Query("SELECT o FROM Order o WHERE o.code = ?1")
	List<Order> getOrderByName(String code);

	@Query("SELECT new OrderModel(o.code, o.address.Fullname, sum(od.quantity), sum(od.book.price * od.quantity), o.date, o.status) " +
		       "FROM Order o " +
		       "JOIN o.orderDetails od " +  // Kết hợp với bảng OrderDetail
		       "JOIN od.book b " +  // Kết hợp với bảng Book để truy xuất price
		       "WHERE o.address.user.email = ?1 " +
		       "GROUP BY o.code, o.address.Fullname, o.date, o.status " +
		       "ORDER BY o.date ASC")
		List<OrderModel> listOrderHistory(String email);


	@Query("SELECT new OrderModel(o.code, o.address.Fullname, sum(od.quantity), sum(od.book.price * od.quantity), o.date, o.status) " +
		       "FROM Order o " +
		       "JOIN o.orderDetails od " +  // Kết hợp với bảng OrderDetail
		       "JOIN od.book b " +  // Kết hợp với bảng Book để truy xuất price
		       "WHERE o.status = 0 " +
		       "GROUP BY o.code, o.address.Fullname, o.date, o.status " +
		       "ORDER BY o.date ASC")
		List<OrderModel> listOrderGroupByCodePending();


	@Query("SELECT new OrderModel(o.code, o.address.Fullname, sum(od.quantity), sum(od.book.price * od.quantity), o.date, o.status) " +
		       "FROM Order o " +
		       "JOIN o.orderDetails od " +  // Kết hợp với bảng OrderDetail
		       "JOIN od.book b " +  // Kết hợp với bảng Book để truy xuất price
		       "WHERE o.status = 1 " +
		       "GROUP BY o.code, o.address.Fullname, o.date, o.status " +
		       "ORDER BY o.date ASC")
		List<OrderModel> listOrderGroupByCodeShipping();


	@Query("SELECT new poly.store.model.OrderModel(o.code, o.address.Fullname, SUM(od.quantity), SUM(od.book.price * od.quantity), o.date, o.status) " +
		       "FROM Order o " +
		       "JOIN o.orderDetails od " +  // Kết hợp với bảng OrderDetail
		       "JOIN od.book b " +  // Kết hợp với bảng Book để truy xuất giá (price)
		       "WHERE o.status = 2 " +  // Trạng thái thành công
		       "GROUP BY o.code, o.address.Fullname, o.date, o.status " +  // Nhóm theo các trường đã chỉ định
		       "ORDER BY o.date ASC")
		List<OrderModel> listOrderGroupByCodeSuccess();




	@Query("SELECT new OrderModel(o.code, o.address.Fullname, sum(od.quantity), sum(od.book.price * od.quantity), o.date, o.status) " +
		       "FROM Order o " +
		       "JOIN o.orderDetails od " +  // Kết hợp với bảng OrderDetail
		       "JOIN od.book b " +  // Kết hợp với bảng Book để truy xuất price
		       "WHERE o.status = 3 " +
		       "GROUP BY o.code, o.address.Fullname, o.date, o.status " +
		       "ORDER BY o.date ASC")
		List<OrderModel> listOrderGroupByCodeCancel();


	@Query("SELECT o FROM Order o WHERE o.code = ?1 and o.address.user.email = ?2")
	List<Order> listOrderByCodeAndUsername(String code, String username);

	@Query("SELECT new StatisticalBookDay(od.book.code, od.book.name, od.book.price, od.book.quantity, sum(od.quantity)) " +
		       "FROM Order o " +
		       "JOIN o.orderDetails od " +  // Liên kết với OrderDetails
		       "JOIN od.book b " +  // Liên kết với Book
		       "WHERE CAST(GETDATE() AS date) = o.date " +
		       "AND o.status = 2 " +
		       "GROUP BY od.book.code, od.book.name, od.book.price, od.book.quantity")
		List<StatisticalBookDay> listStatisticalBookDay();



	@Query("SELECT new OrderModel(o.code, o.address.Fullname, sum(od.quantity), sum(od.book.price * od.quantity), o.date, o.status) " +
		       "FROM Order o " +
		       "JOIN o.orderDetails od " +  // Liên kết với OrderDetails
		       "JOIN od.book b " +  // Liên kết với Book
		       "WHERE o.status = 2 " +
		       "AND MONTH(o.date) = ?1 " +
		       "AND YEAR(o.date) = ?2 " +
		       "GROUP BY o.code, o.address.Fullname, o.date, o.status " +
		       "ORDER BY o.date ASC")
		List<OrderModel> listStatisticalRevenueMonth(int month, int year);



	@Query("SELECT new OrderModel(o.code, o.address.Fullname, sum(od.quantity), sum(od.book.price * od.quantity), o.date, o.status) " +
		       "FROM Order o " +
		       "JOIN o.orderDetails od " +  // Liên kết với OrderDetails
		       "JOIN od.book b " +  // Liên kết với Book
		       "WHERE o.status = 2 " +
		       "AND YEAR(o.date) = ?1 " +
		       "GROUP BY o.code, o.address.Fullname, o.date, o.status " +
		       "ORDER BY o.date ASC")
		List<OrderModel> listStatisticalRevenueYear(int year);



	@Query("SELECT new OrderModel(o.code, o.address.Fullname, sum(od.quantity), sum(od.book.price * od.quantity), o.date, o.status) " +
		       "FROM Order o " +
		       "JOIN o.orderDetails od " +  // Liên kết với OrderDetails
		       "JOIN od.book b " +  // Liên kết với Book
		       "WHERE o.status = 2 " +
		       "AND DAY(o.date) = ?1 " +
		       "AND MONTH(o.date) = ?2 " +
		       "AND YEAR(o.date) = ?3 " +
		       "GROUP BY o.code, o.address.Fullname, o.date, o.status " +
		       "ORDER BY o.date ASC")
		List<OrderModel> listStatisticalRevenueDay(int day, int month, int year);



	@Query("SELECT new StatisticalOrder(COUNT(o)) FROM Order o WHERE (o.status = 2) AND DAY(o.date) = ?1 AND MONTH(o.date) = ?2 AND YEAR(o.date) = ?3 GROUP BY o.code")
	List<StatisticalOrder> getMaxOrderSuccessOnDay(int day, int month, int year);

	@Query("SELECT new StatisticalOrder(COUNT(o)) FROM Order o WHERE (o.status = 1) AND DAY(o.date) = ?1 AND MONTH(o.date) = ?2 AND YEAR(o.date) = ?3 GROUP BY o.code")
	List<StatisticalOrder> getMaxOrderTransportOnDay(int day, int month, int year);

	@Query("SELECT new StatisticalOrder(COUNT(o)) FROM Order o WHERE (o.status = 0) AND DAY(o.date) = ?1 AND MONTH(o.date) = ?2 AND YEAR(o.date) = ?3 GROUP BY o.code")
	List<StatisticalOrder> getMaxOrderWaitOnDay(int day, int month, int year);

	@Query("SELECT new StatisticalOrder(COUNT(o)) FROM Order o WHERE (o.status = 3) AND DAY(o.date) = ?1 AND MONTH(o.date) = ?2 AND YEAR(o.date) = ?3 GROUP BY o.code")
	List<StatisticalOrder> getMaxOrderCancelOnDay(int day, int month, int year);

	@Query("SELECT new StatisticalOrder(COUNT(o)) FROM Order o WHERE (o.status = 2) AND MONTH(o.date) = ?1 AND YEAR(o.date) = ?2 GROUP BY o.code")
	List<StatisticalOrder> getMaxOrderSuccessOnMonth(int month, int year);

	@Query("SELECT new StatisticalOrder(COUNT(o)) FROM Order o WHERE (o.status = 1) AND MONTH(o.date) = ?1 AND YEAR(o.date) = ?2 GROUP BY o.code")
	List<StatisticalOrder> getMaxOrderTransportOnMonth(int month, int year);

	@Query("SELECT new StatisticalOrder(COUNT(o)) FROM Order o WHERE (o.status = 0) AND MONTH(o.date) = ?1 AND YEAR(o.date) = ?2 GROUP BY o.code")
	List<StatisticalOrder> getMaxOrderWaitOnMonth(int month, int year);

	@Query("SELECT new StatisticalOrder(COUNT(o)) FROM Order o WHERE (o.status = 3) AND MONTH(o.date) = ?1 AND YEAR(o.date) = ?2 GROUP BY o.code")
	List<StatisticalOrder> getMaxOrderCancelOnMonth(int month, int year);

	@Query("SELECT new StatisticalOrder(COUNT(o)) FROM Order o WHERE (o.status = 2) AND YEAR(o.date) = ?1 GROUP BY o.code")
	List<StatisticalOrder> getMaxOrderSuccessOnYear(int year);

	@Query("SELECT new StatisticalOrder(COUNT(o)) FROM Order o WHERE (o.status = 1) AND YEAR(o.date) = ?1 GROUP BY o.code")
	List<StatisticalOrder> getMaxOrderTransportOnYear(int year);

	@Query("SELECT new StatisticalOrder(COUNT(o)) FROM Order o WHERE (o.status = 0) AND YEAR(o.date) = ?1 GROUP BY o.code")
	List<StatisticalOrder> getMaxOrderWaitOnYear(int year);

	@Query("SELECT new StatisticalOrder(COUNT(o)) FROM Order o WHERE (o.status = 3) AND YEAR(o.date) = ?1 GROUP BY o.code")
	List<StatisticalOrder> getMaxOrderCancelOnYear(int year);

	@Query("SELECT MAX(YEAR(o.date)) FROM Order o")
	int getMaxYearOrder();

	@Query("SELECT MIN(YEAR(o.date)) FROM Order o")
	int getMinYearOrder();

	@Query("SELECT new poly.store.model.BestSellerModel(b, SUM(od.quantity)) " +
		       "FROM OrderDetail od " +
		       "JOIN od.book b " +
		       "WHERE b.Deleteday IS NULL AND b.active = TRUE " +
		       "GROUP BY b " +
		       "ORDER BY SUM(od.quantity) DESC")
		List<BestSellerModel> getListBestSellerBook(Pageable pageable);


	@Query("SELECT COUNT(DISTINCT o.code) FROM Order o WHERE o.status = 0 ")
	int countPendingOrders();

	@Query("SELECT COUNT(DISTINCT o.code) FROM Order o WHERE o.status = 1")
	int countShippingOrders();

	@Query("SELECT COUNT(DISTINCT o.code) FROM Order o WHERE o.status = 2")
	int countSuccessOrders();

	@Query("SELECT COUNT(DISTINCT o.code) FROM Order o WHERE o.status = 3")
	int countCancelOrders();

	@Query("SELECT a.user.id, COUNT(o.id) " + "FROM Order o " + "JOIN Address a ON o.address.id = a.id "
			+ "WHERE o.status = 2 " + "GROUP BY a.user.id " + "HAVING COUNT(o.id) >= 3")
	List<Object[]> findUsersWithSuccessfulOrders();
	
	@Query("SELECT o FROM Order o WHERE o.code = :code")
	List<Order> findByCode(@Param("code") String code);

}
