/**
 * @(#)RegisterFormValidator.java 2021/09/08.
 * 
 * Copyright(C) 2021 by PHOENIX TEAM.
 * 
 * Last_Update 2021/09/08.
 * Version 1.00.
 */
package poly.store.validator.user;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import poly.store.entity.User;
import poly.store.model.UserRegister;
import poly.store.service.UserService;

/**
 * Class bat loi form register.html
 * 
 * @author khoa-ph
 * @version 1.00
 */
@Component
public class RegisterFormValidator implements Validator {

	// Thong tin user service
	@Autowired
	UserService userService;

	private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(gmail\\.com|yahoo\\.com|outlook\\.com)$";
	// Kiem tra dinh dang email
	private EmailValidator emailValidator = EmailValidator.getInstance();
	/**
	 * Lien ket class UserLogin voi class bat loi
	 * 
	 * @param clazz
	 * @return Ket qua co dung hay khong
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz == UserRegister.class;
	}

	/**
	 * Kiem tra form co thoa dieu kien
	 * 
	 * @param target
	 * @param errors
	 */
	@Override
	public void validate(Object target, Errors errors) {
		// Lien ket Object voi UserRegister class
		UserRegister userRegister = (UserRegister) target;
		// Kiem tra co bo trong field ho va ten
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fullName", "NotBlank.userRegister.fullname");
		// Kiem tra co bo trong field email
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotBlank.userRegister.email");
		// Kiem tra co bo trong field mat khau
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotBlank.userRegister.password");
		// Kiem tra co bo trong field xac nhan mat khau
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "NotBlank.userRegister.confirmPassword");

		if (!errors.hasFieldErrors("email")) {
		    String email = userRegister.getEmail();

		    // Kiểm tra email có đúng định dạng và đúng domain
		    if (!email.matches(EMAIL_PATTERN)) {
		        errors.rejectValue("email", "NotPattern.userRegister.email");
		    } else {
		        // Kiểm tra email đã tồn tại chưa
		        User user = userService.findUserByEmail(email);
		        if (user != null) {
		            errors.rejectValue("email", "Duplicate.userRegister.email");
		        }
		    }
		}

		// Kiểm tra mật khẩu có đúng yêu cầu không
	    if (!errors.hasFieldErrors("password")) {
	        String password = userRegister.getPassword();

	        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,30}$")) {
	            errors.rejectValue("password", "Format.userRegister.password",
	                "Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa, chữ thường và ký tự đặc biệt!");
	        }
	        if (userRegister.getPassword().length() > 30) {
 				errors.rejectValue("password", "Max.userRegister.password");
 			}
	    }
	 // Kiem tra mat khau co dung dinh dang
//	 		if (!errors.hasFieldErrors("password")) {
//	 			if (userRegister.getPassword().length() < 7) {
//	 				errors.rejectValue("password", "Min.userRegister.password");
//	 			}
//	 			if (userRegister.getPassword().length() > 30) {
//	 				errors.rejectValue("password", "Max.userRegister.password");
//	 			}
//	 		}


		// Kiem tra xac nhan mat khau co trung khop
		if (!errors.hasFieldErrors("confirmPassword")) {
			if (!userRegister.getPassword().equals(userRegister.getConfirmPassword())) {
				errors.rejectValue("confirmPassword", "NotDuplicate.userRegister.confirmPassword");
			}
		}
	}

}
