package poly.store.controller.user;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import poly.store.common.Constants;
import poly.store.entity.Book;
import poly.store.entity.Favorite;
import poly.store.entity.Order;
import poly.store.entity.OrderDetail;
import poly.store.model.AlertModel;
import poly.store.model.OrderModel;
import poly.store.service.FavoriteService;
import poly.store.service.OrderService;
import poly.store.service.ParamService;
import poly.store.service.UserService;
import poly.store.service.impl.ShoppingCartServiceImpl;

@Controller
public class AccountController {
	@Autowired
	UserService userService;

	@Autowired
	FavoriteService favoriteService;

	@Autowired
	OrderService orderService;

	@Autowired
	ShoppingCartServiceImpl cartService;

	@Autowired
	ParamService paramService;

	@Autowired
	HttpSession session; // Dùng session để lưu phí ship tạm thời

	@GetMapping("/account")
	public String index() {
		return Constants.USER_DISPLAY_ACCOUNT_PAGE;
	}

	@GetMapping("/account/information")
	public String information(Model model) {
		return Constants.USER_DISPLAY_ACCOUNT_INFORMATION;
	}

	@GetMapping("/account/favorite")
	public String favorite(Model model) {

		List<Favorite> listFavorite = favoriteService.getListFavoriteByEmail();

		model.addAttribute("listFavorite", listFavorite);
		return Constants.USER_DISPLAY_ACCOUNT_FAVORITE;
	}

	@GetMapping("/account/favorite/delete/{id}")
	public String deleteFavorite(@PathVariable("id") int id, Model model) {

		favoriteService.delete(id);

		return "redirect:/account/favorite";
	}

	@GetMapping("/account/order")
	public String order(Model model) {
	    List<OrderModel> listOrderHistory = orderService.listOrderHistory();

	    for (OrderModel list : listOrderHistory) {
	        Order order = orderService.getOrderByName(list.getId()).get(0);
	        if (order != null) {
	            list.setDiscount(order.getDiscount());
	            list.setShippingFee(order.getShippingFee()); // Gán phí ship vào danh sách

	            // Tính tổng tiền, kiểm tra giá sale của mỗi sản phẩm
	            long total = 0; // Chuyển đổi sang kiểu long cho phù hợp với OrderModel (Long total)
	            for (OrderDetail orderDetail : order.getOrderDetails()) {
	                Book book = orderDetail.getBook();
	                // Kiểm tra nếu có giá sale, sử dụng giá sale, nếu không có thì sử dụng giá gốc
	                int price = (book.getSales() != 0 && book.getSales() > 0) ? book.getSales() : book.getPrice();
	                // Tính tổng giá trị
	                total += price * orderDetail.getQuantity(); // Sử dụng long cho tổng giá trị
	            }
	            list.setTotal(total); // Gán tổng vào OrderModel, kiểu Long
	        }
	    }
	    model.addAttribute("listOrder", listOrderHistory);

	    return Constants.USER_DISPLAY_ACCOUNT_ORDER;
	}

	@GetMapping("/account/order/invoice/{id}")
	public String invoice(@PathVariable("id") String id, Model model) {
		// Lấy danh sách đơn hàng theo mã
		List<Order> list = orderService.listOrderByCodeAndUsername(id);

		if (list.isEmpty()) {
			return Constants.USER_DISPLAY_404_PAGE;
		} else {
			// Lấy phí ship từ đơn hàng đầu tiên (nếu có)
			Integer shippingFee = list.get(0).getShippingFee() != null ? list.get(0).getShippingFee() : 30000;

			int total = 0;
			int discount = 0;

			// Duyệt qua danh sách đơn hàng và tính tổng giá trị
			for (Order order : list) {
				// Lấy danh sách chi tiết đơn hàng (OrderDetail)
				List<OrderDetail> orderDetails = order.getOrderDetails(); // Giả sử Order có phương thức //
																			// getOrderDetails()

				for (OrderDetail orderDetail : orderDetails) {
					// Lấy đối tượng Book từ OrderDetail
					Book book = orderDetail.getBook();

					// Kiểm tra nếu có giá sale, nếu có thì dùng giá sale, nếu không thì dùng giá
					// gốc
					int price = (book.getSales() != 0) ? book.getSales() : book.getPrice();

					// Tính tổng giá trị sử dụng giá sale (nếu có) hoặc giá gốc
					total += price * orderDetail.getQuantity();
				}
			}

			// Kiểm tra và lấy discount nếu có
			if (list.get(0).getDiscount() != null) {
				discount = list.get(0).getDiscount().getPrice();
			}

			// Thêm thông tin vào model
			model.addAttribute("shippingFee", shippingFee);
			model.addAttribute("listBook", list);
			model.addAttribute("total", total);
			model.addAttribute("discount", discount);
		}

		return Constants.USER_DISPLAY_ACCOUNT_INVOICE;
	}

	@GetMapping("/account/order/cancel/{id}")
	public String cancel(@PathVariable("id") String id, Model model) {
		orderService.cancelOrder(id);
		return "redirect:/account/order";
	}

	@GetMapping("/account/order/search")
	public String search(Model model) {
		AlertModel alertModel = new AlertModel();
		model.addAttribute("alertModel", alertModel);
		return Constants.USER_DISPLAY_ACCOUNT_ORDER_SEARCH;
	}

	@PostMapping("/account/order/search")
	public String searchByCode(Model model) {
		AlertModel alertModel = new AlertModel();
		String code = paramService.getString("code", "");

		if (code.trim().isEmpty()) {
			alertModel.setAlert("alert-warning");
			alertModel.setContent("Vui lòng nhập mã đơn hàng!");
			alertModel.setDisplay(true);
		} else {
			List<Order> list = orderService.listOrderByCodeAndUsername(code);
			if (list.isEmpty()) {
				alertModel.setAlert("alert-warning");
				alertModel.setContent("Mã đơn hàng không tồn tại!");
				alertModel.setDisplay(true);
			} else {
				return "redirect:/account/order/invoice/" + code;
			}
		}

		model.addAttribute("code", code.trim());
		model.addAttribute("alertModel", alertModel);

		return Constants.USER_DISPLAY_ACCOUNT_ORDER_SEARCH;
	}
}
