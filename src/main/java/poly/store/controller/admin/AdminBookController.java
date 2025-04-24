
package poly.store.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import poly.store.common.Constants;

/**
 * Class dung de quan ly san pham
 * 
 * @author 
 * @version 
 */
@Controller
public class AdminBookController {
	/**
	 * Hien thi trang chu cua giao dien san pham
	 * 
	 * @return trang quan ly san pham
	 */
	@GetMapping("/admin/book/form")
	public String form(Model model) {
		model.addAttribute("enableBtnUpdate", false);
		return Constants.USER_DISPLAY_ADMIN_BOOK_FORM;
	}
	
	/**
	 * Hien thi trang chu cua giao dien nguoi dung
	 * 
	 * @return trang quan ly nhan vien
	 */
	@GetMapping("/admin/book/list")
	public String list(Model model) {
		return Constants.USER_DISPLAY_ADMIN_BOOK_LIST;
	}
		
	@GetMapping("/admin/book/update/{id}")
	public String update(Model model, @PathVariable("id") Integer id) {
		model.addAttribute("bookId", id);
		model.addAttribute("enableBtnUpdate", true);
		return Constants.USER_DISPLAY_ADMIN_BOOK_FORM;
	}
}
