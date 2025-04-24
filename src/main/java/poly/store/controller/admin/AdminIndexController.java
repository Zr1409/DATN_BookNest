
package poly.store.controller.admin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import poly.store.common.Constants;
import poly.store.model.StatisticalRevenue;
import poly.store.service.OrderService;
import poly.store.service.BookService;
import poly.store.service.UserService;

/**
 * Class de hien thi trang chu quan tri
 * 
 * @author 
 * @version 
 */
@Controller
public class AdminIndexController {
	@Autowired
	OrderService orderService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	BookService bookService;
	
	
	/**
	 * Hien thi trang chu cua giao dien nguoi dung
	 * 
	 * @return trang admin index.html
	 */
	@GetMapping("/admin/index")
	public String index(Model model) {
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
	    Date date = new Date();
	    String[] today = formatter.format(date).split("-");

	    int currentMonth = Integer.parseInt(today[1]); // Lấy tháng hiện tại
	    int currentYear = Integer.parseInt(today[0]);  // Lấy năm hiện tại

	    // Lấy số đơn hàng thành công trong tháng hiện tại
	    long order = orderService.getStatisticalTotalOrderOnMonth(currentMonth, currentYear).getOrderSuccess();

	    // Lấy danh sách doanh thu theo tháng của năm hiện tại
	    List<StatisticalRevenue> listRevenue = orderService.listStatisticalRevenueByMonth(currentMonth, currentYear);

	    // Chỉ lấy doanh thu của tháng hiện tại
	    double revenue = 0;
	    for (StatisticalRevenue item : listRevenue) {
	        if (item.getDate() == currentMonth) { 
	            revenue = item.getPrice(); // Lấy doanh thu của tháng hiện tại
	            break; // Không cần duyệt tiếp, vì chỉ cần doanh thu tháng đó thôi
	        }
	    }

	    long customer = userService.findAllUserNotRoleAdmin().size();
	    long product = bookService.findAll().size();

	    model.addAttribute("product", product);
	    model.addAttribute("customer", customer);
	    model.addAttribute("revenue", revenue);
	    model.addAttribute("order", order);

	    return Constants.USER_DISPLAY_ADMIN_INDEX;
	}

	
}
