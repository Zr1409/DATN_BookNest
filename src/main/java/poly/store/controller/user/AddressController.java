package poly.store.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import poly.store.common.Constants;
import poly.store.entity.Address;
import poly.store.service.AddressService;

@Controller
public class AddressController {
	@Autowired
	AddressService addressService;
	
	
	@GetMapping("/account/address")
	public String index() {
		return Constants.USER_DISPLAY_ACCOUNT_ADDRESS;
	}
	
	@GetMapping("/account/address/add")
	public String add(Model model) {
		model.addAttribute("enableBtnUpdate", false);
		return Constants.USER_DISPLAY_ACCOUNT_ADDRESS_ADD;
	}
	
	@GetMapping("/account/address/delete/{id}")
	public String add(@PathVariable("id") int id) {
		Address address = addressService.getAddressById(id);
		
		addressService.delete(address);
		
		return "redirect:/account/address";
	}
	
	@GetMapping("/account/address/update/{id}")
	public String update(@PathVariable("id") int id, Model model) {
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
		
		Address address = addressService.findAddressById(username, id);
		
		if(address == null) {
			return "redirect:/account/address";
		}
		
		model.addAttribute("addressId", id);
		model.addAttribute("enableBtnUpdate", true);
		return Constants.USER_DISPLAY_ACCOUNT_ADDRESS_UPDATE;
	}
	
	@ModelAttribute("listAddress")
	public List<Address> getListAddress(Model model) {
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
		return addressService.findListAddressByEmail(username);
	}
}
