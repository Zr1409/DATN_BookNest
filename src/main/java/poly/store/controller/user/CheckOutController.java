package poly.store.controller.user;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import poly.store.common.Constants;
import poly.store.entity.Address;
import poly.store.entity.Discount;
import poly.store.entity.Order;
import poly.store.entity.OrderDetail;
import poly.store.entity.Payment;
import poly.store.entity.User;
import poly.store.entity.Wallet;
import poly.store.entity.WalletTransaction;
import poly.store.entity.Book;
import poly.store.model.CartModel;
import poly.store.model.DetailOrder;
import poly.store.service.AddressService;
import poly.store.service.DiscountService;
import poly.store.service.OrderDetailService;
import poly.store.service.OrderService;
import poly.store.service.ParamService;
import poly.store.service.BookService;
import poly.store.service.SessionService;
import poly.store.service.VNPAYService;
import poly.store.service.impl.MailerServiceImpl;
import poly.store.service.impl.PaymentServiceImpl;
import poly.store.service.impl.ShoppingCartServiceImpl;
import poly.store.service.impl.UserServiceImpl;
import poly.store.service.impl.WalletServiceImpl;
import poly.store.service.impl.WalletTransactionServiceImpl;

@Controller
public class CheckOutController {
	@Autowired
	AddressService addressService;

	@Autowired
	AddressService provinceService;

	@Autowired
	ShoppingCartServiceImpl cartService;

	@Autowired
	DiscountService discountService;

	@Autowired
	SessionService sessionService;

	@Autowired
	ParamService paramService;

	@Autowired
	OrderService orderService;

	@Autowired
	OrderDetailService orderDetailService;

	@Autowired
	BookService bookService;

	@Autowired
	MailerServiceImpl mailerService;

	@Autowired
	VNPAYService vnpayService;

	@Autowired
	PaymentServiceImpl paymentService;

	@Autowired
	UserServiceImpl userServiceImpl;

	@Autowired
	WalletServiceImpl walletServiceImpl;

	@Autowired
	WalletTransactionServiceImpl walletTransactionServiceImpl;

	@Autowired
	private HttpSession session; // Dùng session để lưu phí ship tạm thời

	@GetMapping("/shop/cart/checkout")
	public String index(Model model) {
		List<CartModel> listCartModel = new ArrayList<>(cartService.getItems());
		if (listCartModel.isEmpty()) {
			return "redirect:/shop/cart";
		}
		// Kiểm tra giá trị trong session
		Integer shippingFee = (Integer) session.getAttribute("shippingFee");
		System.out.println("Phí ship trong session khi vào checkout: " + shippingFee);

		if (shippingFee == null) {
			shippingFee = 30000; // Mặc định 30k nếu chưa có
		}

		// Đưa phí ship vào model để hiển thị trên Thymeleaf
		model.addAttribute("shippingFee", shippingFee);
		model.addAttribute("cart", cartService);
		return Constants.USER_DISPLAY_CHECKOUT;
	}

	@PostMapping("/shop/cart/checkout")
	public String order(Model model, HttpServletRequest request) {
		String addressId = paramService.getString("address_id", "");
		String method = paramService.getString("shipping_method", "");
		String comment = paramService.getString("comment", null);

		Address address = addressService.getAddressById(Integer.parseInt(addressId));

		Discount discount = cartService.getDiscount();
		if (discount.getId() == 0) {
			discount = null;
		}

		// Lấy phí ship từ session
		Integer shippingFee = (Integer) request.getSession().getAttribute("shippingFee");
		if (shippingFee == null) {
			shippingFee = 30000; // Giá trị mặc định nếu không có trong session
		}

		int code;
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String strDate = formatter.format(date);

		while (true) {
			code = (int) Math.floor(((Math.random() * 899999) + 100000));
			List<Order> list = orderService.getOrderByName(String.valueOf(code));
			if (list.isEmpty()) {
				break;
			}
		}

		// Lưu đơn hàng trước
		Order order = new Order();
		order.setCode(String.valueOf(code));
		order.setAddress(address);
		order.setDiscount(discount);
		order.setDate(strDate);
		order.setStatus("0");
		order.setComment(comment);
		order.setShippingFee(shippingFee);
		if ("1".equals(method)) {
			order.setMethod("Thanh toán tiền mặt");
		} else if ("0".equals(method)) {
			order.setMethod("Thanh toán online");
		} else {
			order.setMethod("Không xác định");
		}

		// Lưu Order trước
		orderService.save(order);

		// Lưu các OrderDetail cho từng sản phẩm trong giỏ hàng
		List<CartModel> listCartModel = new ArrayList<>(cartService.getItems());
		for (CartModel cart : listCartModel) {
			OrderDetail orderDetail = new OrderDetail();
			Book book = cart.getBook();
			// Kiểm tra giá sale, nếu có thì dùng giá sale, nếu không có thì dùng giá gốc
			int price = book.getSales() != 0 ? book.getSales() : book.getPrice();
			orderDetail.setOrder(order); // Gắn order đã lưu cho orderDetail
			orderDetail.setBook(book);
			orderDetail.setQuantity(cart.getQuantity());
			orderDetail.setPrice(price); // Lưu giá sách (có thể là sales hoặc price gốc)
			// Lưu OrderDetail
			orderDetailService.save(orderDetail);

			// Cập nhật số lượng sản phẩm nếu thanh toán tiền mặt
			if ("1".equals(method)) {
				book.setQuantity(book.getQuantity() - cart.getQuantity());
				bookService.updateQuality(book);
			}
		}

		// Lưu mã đơn hàng vào session
		request.getSession().setAttribute("orderCode", String.valueOf(code));

		// Xử lý thanh toán online (nếu có)
		if ("0".equals(method)) {
			double discountAmount = (discount != null) ? discount.getPrice() : 0;
			int orderTotal = tolal() + shippingFee - (int) discountAmount;
			//String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
			// Cách 1: Tự lấy domain, hoạt động cả local và production
			//String baseUrl = request.getScheme() + "://" + request.getServerName()
	       //+ (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort());
		    // Cách 2 (an toàn khi deploy): Hard-code domain thật
		    String baseUrl = "https://datn-booknest.onrender.com";
			
			String vnpayUrl = vnpayService.payment(request, orderTotal, "Thanh toán đơn hàng" + code, baseUrl);
			return "redirect:" + vnpayUrl;
		}

		// Tạo nội dung email
		double discountAmount = (discount != null) ? discount.getPrice() : 0;
		int orderTotal = tolal() + shippingFee - (int) discountAmount;
		SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MM-yyyy");
		String formattedDate = displayFormat.format(date);

		StringBuilder tableRows = new StringBuilder();
		for (CartModel cart : cartService.getItems()) {
			Book book = cart.getBook();
			int price = book.getSales() != 0 ? book.getSales() : book.getPrice();
			int quantity = cart.getQuantity();
			int total = price * quantity;
			tableRows.append(String.format("<tr>" + "    <td style='padding: 8px; border: 1px solid #ddd;'>%s</td>"
					+ "    <td style='padding: 8px; border: 1px solid #ddd;'>%s</td>"
					+ "    <td style='padding: 8px; border: 1px solid #ddd; text-align: center;'>%d</td>"
					+ "    <td style='padding: 8px; border: 1px solid #ddd; text-align: right;'>%,d</td>"
					+ "    <td style='padding: 8px; border: 1px solid #ddd; text-align: right;'>%,d</td>" + "</tr>",
					book.getName(), // %s - tên sách
					book.getCode(), // %s - mã sách
					quantity, // %d - số lượng
					price, // %,d - giá tiền
					total // %,d - thành tiền
			));
		}
		String emailTemplate = "<html>" + "<head>" + "    <style>" + "        body { font-family: Arial, sans-serif; }"
				+ "        .container { max-width: 600px; margin: auto; padding: 20px; border: 1px solid #eee; border-radius: 10px; }"
				+ "        .title { font-size: 24px; color: #2c3e50; margin-bottom: 10px; }"
				+ "        .section { margin: 20px 0; }" + "        .highlight { color: #e67e22; font-weight: bold; }"
				+ "        .footer { margin-top: 30px; font-size: 14px; color: #888; text-align: left; }"
				+ "        table { width: 100%%; border-collapse: collapse; margin-top: 10px; }"
				+ "        th, td { padding: 8px; border: 1px solid #ddd; text-align: left; }"
				+ "        th { background-color: #f9f9f9; }" + "    </style>" + "</head>" + "<body>"
				+ "    <div class='container'>" + "        <div style='text-align:center; margin-bottom:20px;'>"
				+ "            <img style='width: 65%%' src='https://i.imgur.com/d3bqyiq.png' alt='BookNest Logo'/>"
				+ "        </div>" + "        <div class='title'>Thông Tin Đơn Hàng</div>"
				+ "        <div class='section'>" + "            <p>Xin chào <span class='highlight'>%s</span>,</p>"
				+ "            <p>Bạn đã đặt hàng thành công. Dưới đây là thông tin đơn hàng:</p>" + "        </div>"
				+ "        <div class='section'>" + "            <p><strong>Mã đơn hàng:</strong> %s</p>"
				+ "            <p><strong>Ngày đặt hàng:</strong> %s</p>"
				+ "            <p><strong>Phương thức thanh toán:</strong> %s</p>"
				+ "            <p><strong>Ghi chú:</strong> %s</p>" + "        </div>" + "        <div class='section'>"
				+ "            <p><strong>Người nhận:</strong> %s</p>"
				+ "            <p><strong>Địa chỉ giao hàng:</strong><br> %s, %s, %s, %s</p>" + "        </div>"
				+ "        <div class='section'>" + "            <table>" + "                <thead>"
				+ "                    <tr>" + "                        <th>Tên sản phẩm</th>"
				+ "                        <th>Mã sản phẩm</th>" + "                        <th>Số lượng</th>"
				+ "                        <th>Giá tiền</th>" + "                        <th>Thành tiền</th>"
				+ "                    </tr>" + "                </thead>" + "                <tbody>"
				+ "                    %s" + "                </tbody>" + "            </table>" + "        </div>"
				+ "        <div class='section'>" + "            <p><strong>Phí vận chuyển:</strong> %,d VND</p>"
				+ "            %s"
				+ "            <p><strong>Tổng cộng:</strong> <span class='highlight'>%,d VND</span></p>"
				+ "        </div>" + "        <div class='section'>"
				+ "            <p>Xem chi tiết đơn hàng tại: <a href='http://localhost:8080/account/order/invoice/%s'>link chi tiết</a></p>"
				+ "        </div>" + "        <div class='section'>"
				+ "            <p>Cảm ơn bạn đã mua hàng tại <strong>BookNest</strong>!</p>" + "        </div>"
				+ "        <div class='footer'>" + "            Trân trọng,<br>" + "            BookNest Shop"
				+ "        </div>" + "    </div>" + "</body>" + "</html>";
		String emailContent = String.format(emailTemplate, address.getUser().getFullname(), // %s - Tên người nhận
				code, // %s - Mã đơn hàng
				formattedDate, // %s - Ngày đặt
				order.getMethod(), // %s - Phương thức thanh toán
				(comment != null && !comment.trim().isEmpty()) ? comment : "Không có", // %s - Ghi chú
				address.getUser().getFullname(), // %s - Người nhận
				address.getDetail(), // %s
				address.getWard(), // %s
				address.getDistrict(), // %s
				address.getProvince(), // %s
				tableRows.toString(), // %s - Dòng sản phẩm HTML
				shippingFee, // %,d - Phí ship
				(discount != null
						? "<p><strong>Giảm giá:</strong> -" + String.format("%,d", (int) discountAmount) + " VND</p>"
						: ""), // %s - Dòng giảm giá (nếu có)
				orderTotal, // %,d - Tổng tiền
				code // %s - Mã đơn cho link chi tiết
		);

		// Gửi mail
		mailerService.queue(address.getUser().getEmail(), "Xác nhận đơn hàng #" + code + " tại BookNest", emailContent);
		// Xoá session sau khi lưu vào Order
		request.getSession().removeAttribute("shippingFee");
		discountService.updateQuality(discount);
		cartService.clear();
		cartService.clearDiscount();
		model.addAttribute("cart", cartService);
		return "redirect:/shop/cart/checkout/success";
	}

	@GetMapping("/shop/cart/checkout/success")
	public String success(Model model) {
		return Constants.USER_DISPLAY_CHECKOUT_SUCCESS;
	}

	@ModelAttribute("total")
	public int tolal() {
		List<CartModel> list = new ArrayList<>(cartService.getItems());
		int total = 0;
		for (CartModel i : list) {
			// Lấy đối tượng Book từ CartModel
			Book book = i.getBook();

			// Kiểm tra nếu có giá sale, nếu có thì dùng giá sale, nếu không thì dùng giá
			// gốc
			int price = (book.getSales() != 0) ? book.getSales() : book.getPrice();

			// Tính tổng giá trị sử dụng giá sale (nếu có) hoặc giá gốc
			total += price * i.getQuantity();
		}
		return total;
	}

	@ModelAttribute("listAddress")
	public List<Address> getListAddress(Model model) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		return addressService.findListAddressByEmail(username);
	}

	// Sau khi hoàn tất thanh toán, VNPAY sẽ chuyển hướng trình duyệt về URL này
	@GetMapping("/vnpay-payment-return")
	public String paymentCompleted(HttpServletRequest request, Model model)
			throws ParseException, UnsupportedEncodingException {
		int paymentStatus = vnpayService.paymentReturn(request);
		// ======== LƯU THÔNG TIN THANH TOÁN ==========
		String vnp_PayDate = request.getParameter("vnp_PayDate");
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = formatter.format(date);

		String vnp_TransactionNo = request.getParameter("vnp_TransactionNo");
		String vnp_BankCode = request.getParameter("vnp_BankCode");
		Long vnpAmount = Long.parseLong(request.getParameter("vnp_Amount")); // đơn vị: x100
		// Thêm các tham số thông tin thanh toán
		String vnp_OrderInfo = request.getParameter("vnp_OrderInfo");
		String vnp_TxnRef = request.getParameter("vnp_TxnRef");
		String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
		// Chuyển đổi encoding từ ISO-8859-1 sang UTF-8
		String decodedOrderInfo = new String(vnp_OrderInfo.getBytes("ISO-8859-1"), "UTF-8");

		// Lấy mã đơn hàng từ session
		String orderCode = (String) request.getSession().getAttribute("orderCode");

		if (paymentStatus == 1 && orderCode != null) {
			// Thanh toán thành công
			List<Order> orders = orderService.getOrderByCode(orderCode);

			if (orders != null && !orders.isEmpty()) {
				Set<Discount> updatedDiscounts = new HashSet<>();
				for (Order order : orders) {
					Discount discount = order.getDiscount();

					// Lặp qua danh sách OrderDetail của đơn hàng để cập nhật từng sản phẩm (book)
					for (OrderDetail orderDetail : order.getOrderDetails()) {
						Book book = orderDetail.getBook(); // Lấy sách từ OrderDetail

						// Cập nhật số lượng sản phẩm trong kho
						book.setQuantity(book.getQuantity() - orderDetail.getQuantity());
						bookService.updateQuality(book);

						// Nếu đơn hàng có mã giảm giá, cập nhật số lượng giảm giá
						if (discount != null && !updatedDiscounts.contains(discount)) {
							discountService.updateQuality(discount);
							updatedDiscounts.add(discount);
						}

					}

					// Xóa giỏ hàng
					cartService.clear();
					cartService.clearDiscount();
					model.addAttribute("cart", cartService);
					// Lưu thông tin thanh toán thành công
					Payment payment = new Payment();
					payment.setOrder(order);
					payment.setAmount(BigDecimal.valueOf(vnpAmount / 100.0)); // Chuyển đơn vị về VNĐ
					payment.setStatus("Thành công");
					payment.setPaymentDate(strDate);
					payment.setTransactionNo(vnp_TransactionNo);
					payment.setBankCode(vnp_BankCode);
					paymentService.save(payment);
					// ====== CẬP NHẬT VÍ VÀ LƯU GIAO DỊCH ======
					User customer = order.getAddress().getUser();
					User admin = userServiceImpl.findById(3);

					Wallet customerWallet = walletServiceImpl.findByUser(customer);
					Wallet adminWallet = walletServiceImpl.findByUser(admin);

					BigDecimal totalAmount = BigDecimal.valueOf(vnpAmount / 100.0);

					if (customerWallet != null && adminWallet != null) {
						// Cập nhật số dư ví
						customerWallet.setBalance(customerWallet.getBalance().subtract(totalAmount));
						adminWallet.setBalance(adminWallet.getBalance().add(totalAmount));

						String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
						customerWallet.setLastUpdated(now);
						adminWallet.setLastUpdated(now);

						walletServiceImpl.save(customerWallet);
						walletServiceImpl.save(adminWallet);

						// Ghi lại giao dịch
						WalletTransaction transaction = new WalletTransaction();
						transaction.setFromWallet(customerWallet);
						transaction.setToWallet(adminWallet);
						transaction.setAmount(totalAmount);
						transaction.setType("Thanh toán đơn hàng");
						transaction.setCreateDate(now);
						walletTransactionServiceImpl.save(transaction);
					}

					// Gửi email thông báo cho khách hàng
					// === Chuẩn bị nội dung gửi email xác nhận đơn hàng ===
					Address address = order.getAddress();
					String fullname = address.getUser().getFullname();
					String email = address.getUser().getEmail();
					String comment = order.getComment();
					String code = order.getCode();
					// Kiểm tra giá trị trong session
					Integer shippingFee = (Integer) session.getAttribute("shippingFee");
					System.out.println("Phí ship trong session khi vào checkout: " + shippingFee);

					if (shippingFee == null) {
						shippingFee = 30000; // Mặc định 30k nếu chưa có
					}

					double discountAmount = (discount != null) ? discount.getPrice() : 0;
					int subTotal = order.getOrderDetails().stream().mapToInt(od -> {
						int price = od.getBook().getSales() != 0 ? od.getBook().getSales() : od.getBook().getPrice();
						return price * od.getQuantity();
					}).sum();
					int orderTotal = subTotal + shippingFee - (int) discountAmount;

					SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MM-yyyy");
					String formattedDate = displayFormat.format(new Date());

					StringBuilder tableRows = new StringBuilder();
					for (OrderDetail orderDetail : order.getOrderDetails()) {
						Book book = orderDetail.getBook();
						int price = book.getSales() != 0 ? book.getSales() : book.getPrice();
						int quantity = orderDetail.getQuantity();
						int total = price * quantity;

						tableRows.append(String.format("<tr>"
								+ "<td style='padding: 8px; border: 1px solid #ddd;'>%s</td>"
								+ "<td style='padding: 8px; border: 1px solid #ddd;'>%s</td>"
								+ "<td style='padding: 8px; border: 1px solid #ddd; text-align: center;'>%d</td>"
								+ "<td style='padding: 8px; border: 1px solid #ddd; text-align: right;'>%,d VND</td>"
								+ "<td style='padding: 8px; border: 1px solid #ddd; text-align: right;'>%,d VND</td>"
								+ "</tr>", book.getName(), book.getCode(), quantity, price, total));
					}

					String emailContent = String.format("<html>" + "<head>" + "<style>"
							+ "body { font-family: Arial, sans-serif; }"
							+ ".container { max-width: 600px; margin: auto; padding: 20px; border: 1px solid #eee; border-radius: 10px; }"
							+ ".title { font-size: 24px; color: #2c3e50; margin-bottom: 10px; }"
							+ ".section { margin: 20px 0; }" + ".highlight { color: #e67e22; font-weight: bold; }"
							+ ".footer { margin-top: 30px; font-size: 14px; color: #888; text-align: left; }"
							+ "table { width: 100%%; border-collapse: collapse; margin-top: 10px; }"
							+ "th, td { padding: 8px; border: 1px solid #ddd; text-align: left; }"
							+ "th { background-color: #f9f9f9; }" + "</style>" + "</head>" + "<body>"
							+ "<div class='container'>" + "<div style='text-align:center; margin-bottom:20px;'>"
							+ "<img style='width: 65%%' src='https://i.imgur.com/d3bqyiq.png' alt='BookNest Logo'/>"
							+ "</div>" + "<div class='title'>Thông Tin Đơn Hàng</div>" + "<div class='section'>"
							+ "<p>Xin chào <span class='highlight'>%s</span>,</p>"
							+ "<p>Bạn đã đặt hàng thành công. Dưới đây là thông tin đơn hàng:</p>" + "</div>"
							+ "<div class='section'>" + "<p><strong>Mã đơn hàng:</strong> %s</p>"
							+ "<p><strong>Ngày đặt hàng:</strong> %s</p>"
							+ "<p><strong>Phương thức thanh toán:</strong> %s</p>"
							+ "<p><strong>Ghi chú:</strong> %s</p>" + "</div>" + "<div class='section'>"
							+ "<p><strong>Người nhận:</strong> %s</p>"
							+ "<p><strong>Địa chỉ giao hàng:</strong><br> %s, %s, %s, %s</p>" + "</div>"
							+ "<div class='section'>" + "<table>" + "<thead>" + "<tr>" + "<th>Tên sản phẩm</th>"
							+ "<th>Mã sản phẩm</th>" + "<th>Số lượng</th>" + "<th>Giá tiền</th>" + "<th>Thành tiền</th>"
							+ "</tr>" + "</thead>" + "<tbody>%s</tbody>" + "</table>" + "</div>"
							+ "<div class='section'>" + "<p><strong>Phí vận chuyển:</strong> %,d VND</p>" + "%s"
							+ "<p><strong>Tổng cộng:</strong> <span class='highlight'>%,d VND</span></p>" + "</div>"
							+ "<div class='section'>"
							+ "<p>Xem chi tiết đơn hàng tại: <a href='http://localhost:8080/account/order/invoice/%s'>link chi tiết</a></p>"
							+ "</div>" + "<div class='section'>"
							+ "<p>Cảm ơn bạn đã mua hàng tại <strong>BookNest</strong>!</p>" + "</div>"
							+ "<div class='footer'>Trân trọng,<br>BookNest Shop</div>" + "</div>" + "</body>"
							+ "</html>", fullname, code, formattedDate, order.getMethod(),
							(comment != null && !comment.trim().isEmpty()) ? comment : "Không có", fullname,
							address.getDetail(), address.getWard(), address.getDistrict(), address.getProvince(),
							tableRows.toString(), shippingFee,
							(discount != null
									? "<p><strong>Giảm giá:</strong> -" + String.format("%,d", (int) discountAmount)
											+ " VND</p>"
									: ""),
							orderTotal, code);

					// Gửi email xác nhận đơn hàng
					mailerService.queue(email, "Xác nhận đơn hàng #" + code + " tại BookNest", emailContent);

				}

				// Lấy thông tin chi tiết sản phẩm trong đơn hàng
				DetailOrder orderItems = orderService.getDetailOrderByCode(orderCode);
				model.addAttribute("orderItems", orderItems);

				// Thông tin khách hàng
				model.addAttribute("customerName", orders.get(0).getAddress().getUser().getFullname());
				model.addAttribute("customerPhone", orders.get(0).getAddress().getPhone());
				// Lấy các thông tin địa chỉ
				String addressDetail = orders.get(0).getAddress().getDetail();
				String ward = orders.get(0).getAddress().getWard();
				String district = orders.get(0).getAddress().getDistrict();
				String province = orders.get(0).getAddress().getProvince();
				// Kết hợp các thông tin vào một chuỗi
				String fullAddress = addressDetail + ", " + ward + ", " + district + ", " + province;

				// Thêm mã đơn hàng
				model.addAttribute("orderCode", orderCode);
				// Thêm vào model
				model.addAttribute("customerFullAddress", fullAddress);

				// Phương thức thanh toán
				model.addAttribute("paymentMethod", orders.get(0).getMethod());

				// Ngày đặt hàng
				model.addAttribute("orderDate", new Date());

				// Tạm tính (tổng giá trị sản phẩm)
				// Tạm tính (tổng giá trị sản phẩm)
				int subTotal = orders.stream().flatMap(order -> order.getOrderDetails().stream()) // Lấy danh sách
						.mapToInt(orderDetail -> {
							// Kiểm tra xem giá sales có tồn tại, nếu có sử dụng sales, nếu không sử dụng
							// giá gốc
							int price = orderDetail.getBook().getSales() != 0 ? orderDetail.getBook().getSales()
									: orderDetail.getBook().getPrice();
							return price * orderDetail.getQuantity(); // Tính giá trị sản phẩm
						}).sum();

				model.addAttribute("subTotal", subTotal);

				// Phí vận chuyển (cố định hoặc lấy từ đơn hàng)
				int shippingFee = orders.get(0).getShippingFee(); // Lấy phí ship từ đơn hàng

				model.addAttribute("shippingFee", shippingFee);

				// Giảm giá (nếu có)
				Discount discount = orders.get(0).getDiscount();
				int discountAmount = (discount != null) ? discount.getPrice() : 0;
				model.addAttribute("discount", discountAmount);

				// Tổng tiền
				int total = subTotal + shippingFee - discountAmount;
				model.addAttribute("total", total);

				// Ghi chú
				model.addAttribute("comment", orders.get(0).getComment());

				model.addAttribute("vnp_BankCode", request.getParameter("vnp_BankCode"));

				model.addAttribute("paymentMethod", "VNPAY");

				// Ngày thanh toán
				model.addAttribute("paymentDate", new Date());

				// Xóa mã đơn hàng tạm thời khỏi session
				request.getSession().removeAttribute("orderCode");

				model.addAttribute("vnp_Amount", vnpAmount);
				model.addAttribute("vnp_TxnRef", vnp_TxnRef);
				model.addAttribute("vnp_OrderInfo", decodedOrderInfo);
				model.addAttribute("vnp_ResponseCode", vnp_ResponseCode);
				model.addAttribute("vnp_TransactionNo", vnp_TransactionNo);
				model.addAttribute("vnp_BankCode", vnp_BankCode);
				return "user/vnpay/paymentSuccess"; // Chuyển đến trang thành công
			} else {
				// Nếu không tìm thấy đơn hàng với mã code
				return "user/vnpay/paymentFail"; // Chuyển đến trang thất bại
			}

		} else {
			// Thanh toán thất bại
			List<Order> orders = orderService.getOrderByCode(orderCode);
			if (orders != null && !orders.isEmpty()) {
				for (Order order : orders) {
					order.setStatus("3"); // Xóa đơn hàng khỏi cơ sở dữ liệu
					orderService.save(order); // Gọi phương thức xóa đơn hàng
					Payment payment = new Payment();
					payment.setOrder(order);
					payment.setAmount(BigDecimal.valueOf(vnpAmount / 100.0)); // Không thanh toán
					payment.setStatus("Thất bại");
					payment.setPaymentDate(strDate);
					payment.setTransactionNo(vnp_TransactionNo);
					payment.setBankCode(vnp_BankCode);
					paymentService.save(payment);

				}
			}

			// Xóa mã đơn hàng khỏi session
			request.getSession().removeAttribute("orderCode");
			return "user/vnpay/paymentFail"; // Chuyển đến trang thất bại
		}
	}

}
