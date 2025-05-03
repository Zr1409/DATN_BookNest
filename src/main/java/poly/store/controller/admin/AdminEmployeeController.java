
package poly.store.controller.admin;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import poly.store.common.Constants;

/**
 * Class dung de quan ly nhan vien
 * 
 * @author
 * @version
 */
@Controller
public class AdminEmployeeController {

	/**
	 * Hien thi trang chu cua giao dien nguoi dung
	 * 
	 * @return trang quan ly nhan vien
	 */
	@GetMapping("/admin/employees/list")
	public String list(Model model) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = null;

		// Kiểm tra xem principal có phải là UserDetails hay không
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else if (principal instanceof OAuth2User) {
			// Nếu là OAuth2User (DefaultOAuth2User), lấy username (thường là email)
			OAuth2User oauth2User = (OAuth2User) principal;
			username = (String) oauth2User.getAttribute("email"); // Hoặc sử dụng attribute khác nếu có
		}

		if (username == null) {
			throw new RuntimeException("User not authenticated");
		}
		model.addAttribute("username", username);
		return Constants.USER_DISPLAY_ADMIN_EMPLOYEE_LIST;
	}

	/**
	 * Hien thi trang chu cua giao dien nguoi dung
	 * 
	 * @return trang quan ly nhan vien
	 */
	@GetMapping("/admin/employees/form")
	public String form(Model model) {
		model.addAttribute("enableBtnUpdate", false);
		return Constants.USER_DISPLAY_ADMIN_EMPLOYEE_FORM;
	}

	@GetMapping("/admin/employees/update/{id}")
	public String update(Model model, @PathVariable("id") Integer id) {
		System.out.println(id);
		model.addAttribute("userId", id);
		model.addAttribute("enableBtnUpdate", true);
		return Constants.USER_DISPLAY_ADMIN_EMPLOYEE_FORM;
	}
}
