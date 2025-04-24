package poly.store.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import poly.store.dao.DiscountDao;
import poly.store.dao.OrderDao;
import poly.store.dao.OrderDetailDao;
import poly.store.dao.BookDao;
import poly.store.entity.Discount;
import poly.store.entity.Order;
import poly.store.entity.OrderDetail;
import poly.store.entity.Payment;
import poly.store.entity.Book;
import poly.store.entity.User;
import poly.store.entity.Wallet;
import poly.store.entity.WalletTransaction;
import poly.store.model.BestSellerModel;
import poly.store.model.CartModel;
import poly.store.model.DetailOrder;
import poly.store.model.OrderModel;
import poly.store.model.StatisticalOrder;
import poly.store.model.StatisticalBookDay;
import poly.store.model.StatisticalRevenue;
import poly.store.model.StatisticalTotalOrder;
import poly.store.service.DiscountService;
import poly.store.service.OrderService;
import poly.store.service.UserService;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	OrderDao orderDao;

	@Autowired
	OrderDetailDao orderDetailDao;
	@Autowired
	BookDao bookDao;

	@Autowired
	DiscountDao discountDao;

	@Autowired
	MailerServiceImpl mailerService;

	// Thong tin user service
	@Autowired
	UserService userService;

	@Autowired
	DiscountService discountService;

	@Autowired
	PaymentServiceImpl paymentServiceImpl;

	@Autowired
	UserServiceImpl userServiceImpl;

	@Autowired
	WalletServiceImpl walletServiceImpl;

	@Autowired
	WalletTransactionServiceImpl walletTransactionServiceImpl;

	@Override
	public List<Order> getOrderByName(String code) {
		return orderDao.getOrderByName(code);
	}

	@Override
	public void save(Order order) {
		orderDao.save(order);
	}

	// Phương thức xóa đơn hàng
	@Override
	public void delete(Order order) {
		orderDao.delete(order); // Xóa đơn hàng
	}

	private Set<Integer> sentUsers = new HashSet<>(); // Lưu danh sách User đã nhận mã

	@Override
	public List<OrderModel> listOrderHistory() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();

		List<OrderModel> list = orderDao.listOrderHistory(username);

		for (OrderModel order : list) {
			String[] date = order.getDate().split("-");
			String result = date[2] + "/" + date[1] + "/" + date[0];
			order.setDate(result);
		}

		return list;
	}

	@Override
	public List<Order> listOrderByCodeAndUsername(String id) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();

		List<Order> list = orderDao.listOrderByCodeAndUsername(id, username);

		for (Order order : list) {
			String[] date = order.getDate().split("-");
			String result = date[2] + "/" + date[1] + "/" + date[0];
			order.setDate(result);
		}

		return list;
	}

	@Override
	public List<OrderModel> listOrderGroupByCode() {
		List<OrderModel> listOrder = orderDao.listOrderGroupByCodePending();

		for (OrderModel list : listOrder) {
			Order order = orderDao.getOrderByName(list.getId()).get(0);
			if (order != null) {
				list.setDiscount(order.getDiscount());
				list.setShippingFee(order.getShippingFee() != null ? order.getShippingFee() : 30000); // Gán mặc định
				// Tính tổng tiền với giá sales nếu có
				long total = 0;
				for (OrderDetail orderDetail : order.getOrderDetails()) {
					Book book = orderDetail.getBook();
					int price = (book.getSales() != 0 && book.getSales() > 0) ? book.getSales() : book.getPrice();
					total += price * orderDetail.getQuantity(); // nếu null
				}

				list.setTotal(total); // Gán giá trị tổng đã tính với sales
			}
		}

		return listOrder;
	}

	@Override
	public DetailOrder getDetailOrderByCode(String id) {
		DetailOrder detailOrder = new DetailOrder();
		List<Order> listOrder = orderDao.getOrderByName(id);

		if (listOrder.isEmpty()) {
			return null; // Không tìm thấy đơn hàng
		}

		Order order = listOrder.get(0);
		detailOrder.setId(order.getCode());
		detailOrder.setAddress(order.getAddress().getDetail());
		detailOrder.setComment(order.getComment());
		detailOrder.setDate(order.getDate());

		Discount discount = order.getDiscount();
		detailOrder.setDiscount(discount != null ? discount.getPrice() : 0);

		detailOrder.setDistrict(order.getAddress().getDistrict());
		detailOrder.setFullName(order.getAddress().getUser().getFullname());
		detailOrder.setMethod(order.getMethod());
		detailOrder.setPhone(order.getAddress().getPhone());
		detailOrder.setProvince(order.getAddress().getProvince());
		detailOrder.setWard(order.getAddress().getWard());

		int subTotal = 0;
		int shippingFee = order.getShippingFee() != null ? order.getShippingFee() : 30000;

		// Lấy danh sách sản phẩm từ OrderDetail
		List<OrderDetail> orderDetails = orderDetailDao.getOrderDetailByCode(id);
		List<CartModel> listCartModel = new ArrayList<>();

		for (OrderDetail od : orderDetails) {
			CartModel cartModel = new CartModel();
			cartModel.setBook(od.getBook()); // Lấy sách từ OrderDetail
			cartModel.setQuantity(od.getQuantity()); // Số lượng từ OrderDetail
			listCartModel.add(cartModel);
			// Kiểm tra giá sale
			int price = od.getBook().getSales() != 0 ? od.getBook().getSales() // Nếu có giá sales thì dùng giá sales
					: od.getBook().getPrice(); // Nếu không có giá sales thì dùng giá gốc

			// Cập nhật tổng phụ
			subTotal += price * od.getQuantity();
		}

		int total = subTotal + shippingFee - detailOrder.getDiscount();

		detailOrder.setSubTotal(subTotal);
		detailOrder.setShippingFee(shippingFee);
		detailOrder.setTotal(total);
		detailOrder.setListOrder(listCartModel);

		return detailOrder;
	}

	@Override
	public void approveOrder(String id) {
		List<Order> listOrder = orderDao.getOrderByName(id);
		for (Order list : listOrder) {
			list.setStatus("1");
			orderDao.save(list);

			// Thông tin người dùng
			String fullname = list.getAddress().getUser().getFullname();
			String code = list.getCode();
			String email = list.getAddress().getUser().getEmail();

			String emailContent = String.format("<html>" + "<head>" + "    <style>"
					+ "        body { font-family: Arial, sans-serif; background-color: #f4f4f4; }"
					+ "        .container { max-width: 600px; margin: auto; background: #fff; padding: 20px; border-radius: 10px; }"
					+ "        .header { text-align: center; margin-bottom: 20px; }"
					+ "        .title { font-size: 24px; color: #2c3e50; margin-bottom: 10px; }"
					+ "        .content { font-size: 16px; color: #333; line-height: 1.5; }"
					+ "        .highlight { color: #e67e22; font-weight: bold; }"
					+ "        .footer { margin-top: 30px; font-size: 14px; color: #888; text-align: left; }"
					+ "    </style>" + "</head>" + "<body>" + "    <div class='container'>"
					+ "        <div class='header'>"
					+ "            <img src='https://i.imgur.com/d3bqyiq.png' alt='BookNest Logo' style='width: 60%%;'/>"
					+ "        </div>" + "        <div class='title'>Đơn Hàng Đã Được Xác Nhận</div>"
					+ "        <div class='content'>" + "            <p>Xin chào <span class='highlight'>%s</span>,</p>"
					+ "            <p>Chúng tôi rất vui thông báo rằng đơn hàng của bạn với mã <strong>%s</strong> đã được xác nhận.</p>"
					+ "            <p>Chúng tôi sẽ tiến hành xử lý và giao hàng trong thời gian sớm nhất.</p>"
					+ "            <p>Cảm ơn bạn đã mua hàng tại <strong>BookNest</strong>!</p>" + "        </div>"
					+ "        <div class='footer'>" + "            Trân trọng,<br>" + "            BookNest Shop"
					+ "        </div>" + "    </div>" + "</body>" + "</html>", fullname, code);

			mailerService.queue(email, "Đơn hàng #" + code + " đã được xác nhận", emailContent);

		}
	}

	@Override
	public void cancelOrder(String id) {
		// Lấy danh sách đơn hàng theo mã
		List<Order> listOrder = orderDao.getOrderByName(id);

		if (listOrder.isEmpty()) {
			// Nếu không có đơn hàng nào, không làm gì
			return;
		}

		// Xử lý hủy các đơn hàng trong danh sách
		for (Order order : listOrder) {
			// Giả sử Order có một danh sách OrderDetail thay vì Book trực tiếp
			List<OrderDetail> orderDetails = order.getOrderDetails(); // Lấy các chi tiết đơn hàng

			for (OrderDetail orderDetail : orderDetails) {
				Book book = orderDetail.getBook(); // Lấy sách từ OrderDetail
				book.setQuantity(book.getQuantity() + orderDetail.getQuantity()); // Trả lại số lượng sách
				bookDao.save(book); // Lưu sách đã cập nhật
			}

			order.setStatus("3"); // Đánh dấu đơn hàng là hủy
			orderDao.save(order); // Lưu đơn hàng đã thay đổi
			// 3. Hoàn tiền vào ví
			User customer = order.getAddress().getUser();
			User admin = userServiceImpl.findById(3); // Hoặc tìm admin theo ID

			Wallet customerWallet = walletServiceImpl.findByUser(customer);
			Wallet adminWallet = walletServiceImpl.findByUser(admin);

			// Tính lại totalAmount
			int subTotal = 0;
			int shippingFee = order.getShippingFee() != null ? order.getShippingFee() : 30000;
			int discount = order.getDiscount() != null ? order.getDiscount().getPrice() : 0;

			for (OrderDetail od : order.getOrderDetails()) {
				int price = od.getBook().getSales() != 0 ? od.getBook().getSales() : od.getBook().getPrice();
				subTotal += price * od.getQuantity();
			}

			int total = subTotal + shippingFee - discount;
			BigDecimal totalAmount = BigDecimal.valueOf(total);

			if (customerWallet != null && adminWallet != null) {
				customerWallet.setBalance(customerWallet.getBalance().add(totalAmount));
				adminWallet.setBalance(adminWallet.getBalance().subtract(totalAmount));

				String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				customerWallet.setLastUpdated(now);
				adminWallet.setLastUpdated(now);

				walletServiceImpl.save(customerWallet);
				walletServiceImpl.save(adminWallet);

				// Ghi lại giao dịch hoàn tiền
				WalletTransaction refundTransaction = new WalletTransaction();
				refundTransaction.setFromWallet(adminWallet);
				refundTransaction.setToWallet(customerWallet);
				refundTransaction.setAmount(totalAmount);
				refundTransaction.setType("Hoàn tiền do hủy đơn hàng");
				refundTransaction.setCreateDate(now);

				walletTransactionServiceImpl.save(refundTransaction);
			}
			// ✅ Cập nhật trạng thái thanh toán trong bảng payments
			Payment payment = paymentServiceImpl.findByOrder(order); // Bạn cần viết hàm này
			String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			if (payment != null) {
				payment.setStatus("Hoàn tiền"); // hoặc "HUY"
				payment.setRefundDate(date); // nếu có cột ngày hoàn tiền
				paymentServiceImpl.save(payment);
			}
		}

		// Kiểm tra và cập nhật thông tin discount (nếu có)
		Discount discount = listOrder.get(0).getDiscount();
		if (discount != null) {
			discount.setQuality(discount.getQuality() + 1); // Tăng số lượng discount
			discountDao.save(discount); // Lưu discount đã cập nhật
		}

		// Gửi email thông báo hủy đơn hàng
		Order order = listOrder.get(0); // Lấy đơn hàng đầu tiên từ danh sách
		// Thông tin người dùng
		String fullname = order.getAddress().getUser().getFullname();
		String code = order.getCode();
		String email = order.getAddress().getUser().getEmail();

		String emailContent = String.format("<html>" + "<head>" + "    <style>"
				+ "        body { font-family: Arial, sans-serif; background-color: #f4f4f4; }"
				+ "        .container { max-width: 600px; margin: auto; background: #fff; padding: 20px; border-radius: 10px; }"
				+ "        .header { text-align: center; margin-bottom: 20px; }"
				+ "        .title { font-size: 24px; color: #2c3e50; margin-bottom: 10px; }"
				+ "        .content { font-size: 16px; color: #333; line-height: 1.5; }"
				+ "        .highlight { color: #e67e22; font-weight: bold; }"
				+ "        .footer { margin-top: 30px; font-size: 14px; color: #888; text-align: left; }"
				+ "    </style>" + "</head>" + "<body>" + "    <div class='container'>" + "        <div class='header'>"
				+ "            <img src='https://i.imgur.com/d3bqyiq.png' alt='BookNest Logo' style='width: 60%%;'/>"
				+ "        </div>" + "        <div class='title'>Đơn Hàng Đã Bị Hủy</div>"
				+ "        <div class='content'>" + "            <p>Xin chào <span class='highlight'>%s</span>,</p>"
				+ "            <p>Đơn hàng của bạn với mã <strong>%s</strong> đã được <strong>hủy</strong> theo yêu cầu hoặc do có sự cố trong quá trình xử lý.</p>"
				+ "            <p>Số tiền bạn đã thanh toán sẽ được hoàn lại vào ví trong hệ thống. Nếu cần hỗ trợ thêm, vui lòng liên hệ qua số <strong>0358768117</strong>.</p>"
				+ "            <p>Chúng tôi xin lỗi vì sự bất tiện này và hy vọng sẽ tiếp tục được phục vụ bạn trong tương lai.</p>"
				+ "        </div>" + "        <div class='footer'>" + "            Trân trọng,<br>"
				+ "            BookNest Shop" + "        </div>" + "    </div>" + "</body>" + "</html>", fullname,
				code);

		mailerService.queue(email, "Đơn hàng #" + code + " đã bị hủy", emailContent);

	}

	@Override
	public List<OrderModel> listOrderGroupByCodeShipping() {
		List<OrderModel> listOrder = orderDao.listOrderGroupByCodeShipping();

		for (OrderModel list : listOrder) {
			Order order = orderDao.getOrderByName(list.getId()).get(0);
			if (order != null) {
				list.setDiscount(order.getDiscount());
				list.setShippingFee(order.getShippingFee() != null ? order.getShippingFee() : 30000); // Gán mặc định
				// Tính tổng tiền với giá sales nếu có
				long total = 0;
				for (OrderDetail orderDetail : order.getOrderDetails()) {
					Book book = orderDetail.getBook();
					int price = (book.getSales() != 0 && book.getSales() > 0) ? book.getSales() : book.getPrice();
					total += price * orderDetail.getQuantity(); // nếu null
				}

				list.setTotal(total); // Gán giá trị tổng đã tính với sales
			}

		}

		return listOrder;
	}

	@Override
	public void shippedOrder(String id) {
		List<Order> listOrder = orderDao.getOrderByName(id);
		for (Order list : listOrder) {
			list.setStatus("2");
			orderDao.save(list);

			String fullname = list.getAddress().getUser().getFullname();
			String code = list.getCode();
			String email = list.getAddress().getUser().getEmail();
			String emailContent = String.format("<html>" + "<head>" + "    <style>"
					+ "        body { font-family: Arial, sans-serif; background-color: #f4f4f4; }"
					+ "        .container { max-width: 600px; margin: auto; background: #fff; padding: 20px; border-radius: 10px; }"
					+ "        .header { text-align: center; margin-bottom: 20px; }"
					+ "        .title { font-size: 24px; color: #2c3e50; margin-bottom: 10px; }"
					+ "        .content { font-size: 16px; color: #333; line-height: 1.5; }"
					+ "        .highlight { color: #e67e22; font-weight: bold; }"
					+ "        .footer { margin-top: 30px; font-size: 14px; color: #888; text-align: left; }"
					+ "    </style>" + "</head>" + "<body>" + "    <div class='container'>"
					+ "        <div class='header'>"
					+ "            <img src='https://i.imgur.com/d3bqyiq.png' alt='BookNest Logo' style='width: 60%%;'/>"
					+ "        </div>" + "        <div class='title'>Đơn Hàng Đang Được Vận Chuyển</div>"
					+ "        <div class='content'>" + "            <p>Xin chào <span class='highlight'>%s</span>,</p>"
					+ "            <p>Đơn hàng của bạn với mã <strong>%s</strong> hiện đang trên đường giao đến bạn.</p>"
					+ "            <p>Vui lòng đảm bảo điện thoại luôn sẵn sàng để đơn vị vận chuyển có thể liên hệ khi cần.</p>"
					+ "            <p>Đừng quên kiểm tra sản phẩm trước khi nhận nhé!</p>" + "        </div>"
					+ "        <div class='footer'>" + "            Trân trọng,<br>" + "            BookNest Shop"
					+ "        </div>" + "    </div>" + "</body>" + "</html>", fullname, code);

			mailerService.queue(email, "Đơn hàng #" + code + " đang được vận chuyển", emailContent);

		}
	}

	@Override
	public List<OrderModel> listOrderGroupByCodeSuccess() {
		List<OrderModel> listOrder = orderDao.listOrderGroupByCodeSuccess();

		for (OrderModel list : listOrder) {
			Order order = orderDao.getOrderByName(list.getId()).get(0);
			if (order != null) {
				list.setDiscount(order.getDiscount());
				list.setShippingFee(order.getShippingFee() != null ? order.getShippingFee() : 30000); // Gán mặc định
				// Tính tổng tiền với giá sales nếu có
				long total = 0;
				for (OrderDetail orderDetail : order.getOrderDetails()) {
					Book book = orderDetail.getBook();
					int price = (book.getSales() != 0 && book.getSales() > 0) ? book.getSales() : book.getPrice();
					total += price * orderDetail.getQuantity(); // nếu null
				}

				list.setTotal(total); // Gán giá trị tổng đã tính với sales // nếu null
			}
		}
		// Lấy danh sách khách hàng có từ 3 đơn hàng thành công
		List<Object[]> eligibleUsers = orderDao.findUsersWithSuccessfulOrders();

		// Kiểm tra nếu có khách hàng đủ điều kiện
		if (!eligibleUsers.isEmpty()) {
			Discount discount = discountDao.findById(19).orElse(null); // ID mã giảm giá

			if (discount != null) {
				for (Object[] obj : eligibleUsers) {
					Integer userId = (Integer) obj[0];
					User user = userService.findById(userId);

					if (user != null && !sentUsers.contains(userId)) {
						discountService.sendCodeDiscount(discount.getId(), user);
						sentUsers.add(userId); // Đánh dấu đã gửi
					}
				}
			}
		}

		return listOrder;
	}

	@Override
	public List<OrderModel> listOrderGroupByCodeCancel() {
		List<OrderModel> listOrder = orderDao.listOrderGroupByCodeCancel();

		for (OrderModel list : listOrder) {
			Order order = orderDao.getOrderByName(list.getId()).get(0);
			if (order != null) {
				list.setDiscount(order.getDiscount());
				list.setShippingFee(order.getShippingFee() != null ? order.getShippingFee() : 30000); // Gán mặc định
				// Tính tổng tiền với giá sales nếu có
				long total = 0;
				for (OrderDetail orderDetail : order.getOrderDetails()) {
					Book book = orderDetail.getBook();
					int price = (book.getSales() != 0 && book.getSales() > 0) ? book.getSales() : book.getPrice();
					total += price * orderDetail.getQuantity(); // nếu null
				}

				list.setTotal(total); // Gán giá trị tổng đã tính với sales
										// nếu null
			}
		}

		return listOrder;
	}

	@Override
	public void deleteOrder(String id) {
		List<Order> listOrder = orderDao.getOrderByName(id);
		for (Order list : listOrder) {
			orderDao.delete(list);
		}
	}

	@Override
	public List<StatisticalBookDay> listStatisticalBookDay() {
		return orderDao.listStatisticalBookDay();
	}

	@Override
	public List<StatisticalRevenue> listStatisticalRevenueDay(int day, int month, int year) {
		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.MONTH, month - 1);

		int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		List<StatisticalRevenue> listRevenue = new ArrayList<StatisticalRevenue>();

		for (int i = 1; i <= maxDay; i++) {
			long sum = 0;

			List<OrderModel> listOrder = new ArrayList<OrderModel>();
			listOrder = orderDao.listStatisticalRevenueDay(i, month, year);

			if (!listOrder.isEmpty()) {
				for (OrderModel order : listOrder) {
					Discount discount = order.getDiscount();
					sum = sum + order.getTotal();
					if (discount != null) {
						sum = sum - discount.getPrice();
					}
					sum = sum + 50000;
				}

			}

			double total = (double) sum / 1000000;

			StatisticalRevenue statistical = new StatisticalRevenue();
			statistical.setPrice(total);
			statistical.setDate(i);
			listRevenue.add(statistical);
		}

		return listRevenue;
	}

	@Override
	public List<StatisticalRevenue> listStatisticalRevenueByMonth(int month, int year) {
		List<StatisticalRevenue> listRevenue = new ArrayList<>();

		// Duyệt từ tháng 1 đến tháng 12 để giữ đủ cột trên biểu đồ
		for (int i = 1; i <= 12; i++) {
			long sum = 0;
			List<OrderModel> listOrder = orderDao.listStatisticalRevenueMonth(i, year);

			if (!listOrder.isEmpty()) {
				for (OrderModel order : listOrder) {
					Discount discount = order.getDiscount();
					sum += order.getTotal();
					if (discount != null) {
						sum -= discount.getPrice();
					}
					sum += 50000;
				}
			}

			double total = (double) sum / 1000000;

			StatisticalRevenue statistical = new StatisticalRevenue();
			statistical.setPrice(total);
			statistical.setDate(i); // Đảm bảo cột tương ứng với tháng

			listRevenue.add(statistical);
		}

		return listRevenue;
	}

	@Override
	public List<StatisticalRevenue> listStatisticalRevenueByYear(int year) {
		int minYear = year - 10;
		List<StatisticalRevenue> listRevenue = new ArrayList<StatisticalRevenue>();
		for (int i = 1; i <= 10; i++) {
			long sum = 0;
			List<OrderModel> listOrder = new ArrayList<OrderModel>();
			listOrder = orderDao.listStatisticalRevenueYear(minYear + i);

			if (!listOrder.isEmpty()) {
				for (OrderModel order : listOrder) {
					Discount discount = order.getDiscount();
					sum = sum + order.getTotal();
					if (discount != null) {
						sum = sum - discount.getPrice();
					}
					sum = sum + 50000;
				}

			}

			double total = (double) sum / 1000000;

			StatisticalRevenue statistical = new StatisticalRevenue();
			statistical.setPrice(total);
			statistical.setDate(minYear + i);
			listRevenue.add(statistical);

		}

		return listRevenue;

	}

	@Override
	public StatisticalTotalOrder getStatisticalTotalOrderOnDay(int day, int month, int year) {
		List<StatisticalOrder> orderSuccess = orderDao.getMaxOrderSuccessOnDay(day, month, year);
		List<StatisticalOrder> orderWait = orderDao.getMaxOrderWaitOnDay(day, month, year);
		List<StatisticalOrder> orderTransport = orderDao.getMaxOrderTransportOnDay(day, month, year);
		List<StatisticalOrder> orderCancel = orderDao.getMaxOrderCancelOnDay(day, month, year);

		int success = orderSuccess.size();
		int wait = orderWait.size();
		int transport = orderTransport.size();
		int cancel = orderCancel.size();

		StatisticalTotalOrder totalOrder = new StatisticalTotalOrder(success, cancel, wait, transport);

		return totalOrder;
	}

	@Override
	public StatisticalTotalOrder getStatisticalTotalOrderOnMonth(int month, int year) {
		List<StatisticalOrder> orderSuccess = orderDao.getMaxOrderSuccessOnMonth(month, year);
		List<StatisticalOrder> orderWait = orderDao.getMaxOrderWaitOnMonth(month, year);
		List<StatisticalOrder> orderTransport = orderDao.getMaxOrderTransportOnMonth(month, year);
		List<StatisticalOrder> orderCancel = orderDao.getMaxOrderCancelOnMonth(month, year);

		int success = orderSuccess.size();
		int wait = orderWait.size();
		int transport = orderTransport.size();
		int cancel = orderCancel.size();

		StatisticalTotalOrder totalOrder = new StatisticalTotalOrder(success, cancel, wait, transport);

		return totalOrder;
	}

	@Override
	public StatisticalTotalOrder getStatisticalTotalOrderOnYear(int year) {
		List<StatisticalOrder> orderSuccess = orderDao.getMaxOrderSuccessOnYear(year);
		List<StatisticalOrder> orderWait = orderDao.getMaxOrderWaitOnYear(year);
		List<StatisticalOrder> orderTransport = orderDao.getMaxOrderTransportOnYear(year);
		List<StatisticalOrder> orderCancel = orderDao.getMaxOrderCancelOnYear(year);

		int success = orderSuccess.size();
		int wait = orderWait.size();
		int transport = orderTransport.size();
		int cancel = orderCancel.size();

		StatisticalTotalOrder totalOrder = new StatisticalTotalOrder(success, cancel, wait, transport);

		return totalOrder;
	}

	@Override
	public List<Integer> getListYearOrder() {
		int maxYear = orderDao.getMaxYearOrder();
		int minYear = orderDao.getMinYearOrder();

		List<Integer> listYear = new ArrayList<Integer>();

		for (int i = minYear; i <= maxYear; i++) {
			listYear.add(i);
		}

		return listYear;
	}

	@Override
	public List<StatisticalRevenue> listStatisticalTotalRevenueOnOption(int day, int month, int year) {
		if (day == 0 && month == 0) {
			return listStatisticalRevenueByYear(year);
		} else if (day == 0) {
			return listStatisticalRevenueByMonth(month, year);
		} else {
			return listStatisticalRevenueDay(day, month, year);
		}
	}

	@Override
	public StatisticalTotalOrder getStatisticalTotalOrderOnOption(int day, int month, int year) {
		StatisticalTotalOrder totalOrder = new StatisticalTotalOrder();

		if ((day == 0) && (month == 0)) {
			totalOrder = this.getStatisticalTotalOrderOnYear(year);
		} else if (day == 0) {
			totalOrder = this.getStatisticalTotalOrderOnMonth(month, year);
		} else {
			totalOrder = this.getStatisticalTotalOrderOnDay(day, month, year);
		}

		return totalOrder;
	}

	@Override
	public List<BestSellerModel> getListBestSellerBook(Pageable topFour) {
		return orderDao.getListBestSellerBook(topFour);
	}

	@Override
	public List<Book> listStatisticalBookWarehouse() {
		return bookDao.listStatisticalBookWarehouse();
	}

	@Override
	public int countPendingOrders() {
		return orderDao.countPendingOrders();
	}

	@Override
	public int countShippingOrders() {
		return orderDao.countShippingOrders();
	}

	@Override
	public int countSuccessOrders() {
		return orderDao.countSuccessOrders();
	}

	@Override
	public int countCancelOrders() {
		return orderDao.countCancelOrders();
	}

	// ✅ Thêm phương thức tìm đơn hàng theo mã code
	@Override
	public List<Order> getOrderByCode(String code) {
		return orderDao.findByCode(code);
	}

}
