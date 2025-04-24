
package poly.store.controller.user;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import poly.store.common.Constants;
import poly.store.entity.User;
import poly.store.model.UserRegister;
import poly.store.service.UserService;
import poly.store.service.impl.MailerServiceImpl;

/**
 * Class de lay lai mat khau
 * 
 * @author 
 * @version 
 */
@Controller
public class ForgetPasswordController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	MailerServiceImpl mailerService;
	

	/**
	 * Hien thi man hinh forget-password
	 * 
	 * @param model
	 * @return man hinh forget-password
	 */
	@GetMapping("/forget-password")
	public String displayFormForgetPassword(Model model) {
		UserRegister userForm = new UserRegister();
		model.addAttribute("userForm", userForm);
		return Constants.USER_DISPLAY_FORGET_PASSWORD;
	}

//	@PostMapping("/forget-password")
//	public String handlerFormForgetPassword(Model model, @ModelAttribute("userForm") @Validated UserRegister userForm,
//			BindingResult result) {
//		if (userForm.getEmail().isEmpty()) {
//			result.rejectValue("email", "NotBlank.userRegister.email");
//		} else {
//			User user = userService.findUserByEmail(userForm.getEmail());
//			if (user == null) {
//				result.rejectValue("email", "NotExist.userLogin.username");
//			}
//			else {
//				String password = pe.encode(user.getPassword());
//				mailerService.queue(userForm.getEmail(), "Làm mới mật khẩu!", "Vui lòng click vào link này: "+ "http://localhost:8080/reset-password?code="+password+"&email="+user.getEmail() +" để reset mật khẩu.");
//			}
//		}
//
//		if (result.hasErrors()) {
//			return Constants.USER_DISPLAY_FORGET_PASSWORD;
//		}
//		
//		model.addAttribute("alert", "Thông báo!");
//		model.addAttribute("message", "Vui lòng kiểm tra email để thay đổi mật khẩu!");
//		return Constants.USER_DISPLAY_ALERT_STATUS;
//	}
	@PostMapping("/forget-password")
	public String handlerFormForgetPassword(Model model, @ModelAttribute("userForm") @Validated UserRegister userForm,
	        BindingResult result) {

	    if (userForm.getEmail().isEmpty()) {
	        result.rejectValue("email", "NotBlank.userRegister.email");
	    } else {
	        User user = userService.findUserByEmail(userForm.getEmail());

	        if (user == null) {
	            result.rejectValue("email", "NotExist.userRegister.email");
	        } else {
	            // Tạo mật khẩu ngẫu nhiên
	            String randomPassword = generateRandomPassword(8);

	            // Mã hóa mật khẩu
	            //String encodedPassword = pe.encode(randomPassword);

	            // Cập nhật mật khẩu mới cho user
	            user.setPassword(randomPassword );
	            userService.save(user);

	            // Gửi email chứa mật khẩu mới
	            mailerService.queue(userForm.getEmail(), "Cấp lại mật khẩu!", 
	                "Mật khẩu mới của bạn là: " + randomPassword + 
	                "\n.Vui lòng đăng nhập và đổi lại mật khẩu ngay!");

	            model.addAttribute("alert", "Thông báo!");
	            model.addAttribute("message", "Vui lòng kiểm tra email để nhận mật khẩu mới!");
	            return Constants.USER_DISPLAY_ALERT_STATUS;
	        }
	    }

	    if (result.hasErrors()) {
	        return Constants.USER_DISPLAY_FORGET_PASSWORD;
	    }

	    return Constants.USER_DISPLAY_ALERT_STATUS;
	}

	// Hàm tạo mật khẩu ngẫu nhiên
	private String generateRandomPassword(int length) {
	    String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    String lowerCase = "abcdefghijklmnopqrstuvwxyz";
	    String specialChars = "@#$%^&*!";
	    String allChars = upperCase + lowerCase + "0123456789" + specialChars;
	    
	    SecureRandom random = new SecureRandom();
	    StringBuilder password = new StringBuilder();

	    // Đảm bảo có ít nhất 1 chữ hoa, 1 chữ thường và 1 ký tự đặc biệt
	    password.append(upperCase.charAt(random.nextInt(upperCase.length())));
	    password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
	    password.append(specialChars.charAt(random.nextInt(specialChars.length())));

	    // Tạo các ký tự ngẫu nhiên còn lại
	    for (int i = 3; i < length; i++) {
	        password.append(allChars.charAt(random.nextInt(allChars.length())));
	    }

	    // Trộn ngẫu nhiên để tránh thứ tự cố định
	    return shuffleString(password.toString());
	}

	// Hàm trộn chuỗi để mật khẩu không theo mẫu cố định
	private String shuffleString(String input) {
	    List<Character> characters = new ArrayList<>();
	    for (char c : input.toCharArray()) {
	        characters.add(c);
	    }
	    Collections.shuffle(characters);
	    StringBuilder shuffled = new StringBuilder();
	    for (char c : characters) {
	        shuffled.append(c);
	    }
	    return shuffled.toString();
	}

}
