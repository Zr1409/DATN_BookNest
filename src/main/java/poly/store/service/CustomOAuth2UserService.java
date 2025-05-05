package poly.store.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import poly.store.dao.DiscountDao;
import poly.store.entity.Discount;
import poly.store.entity.Role;
import poly.store.entity.User;
import poly.store.entity.UserRole;
import poly.store.entity.Wallet;
import poly.store.service.impl.UserRoleServiceImpl;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	@Autowired
	private UserService userService;

	@Autowired
	@Lazy
	private PasswordEncoder passwordEncoder;

	// Thong tin wallet service
	@Autowired
	WalletService walletService;

	// Thong tin user role service
	@Autowired
	UserRoleService userRoleService;
	
	@Autowired
	UserRoleServiceImpl userRoleServiceImpl;

	// Thong tin role service
	@Autowired
	RoleService roleService;

	@Autowired
	DiscountService discountService;

	@Autowired
	DiscountDao discountDao;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {
		OAuth2User oauth2User = super.loadUser(userRequest);

		String email = oauth2User.getAttribute("email");
		String name = oauth2User.getAttribute("name");

		if (email == null) {
			throw new RuntimeException("Email not found from OAuth2 provider");
		}

		// Tạo user nếu chưa có
		User user = userService.findUserByEmail(email);
		if (user == null) {
			user = new User();
			user.setEmail(email);
			user.setFullname(name);
			// Mã hóa mật khẩu trước khi lưu
			String encodedPassword = passwordEncoder.encode("1234567");
			user.setPassword(encodedPassword);
			userService.create(user); // Tạo user mới
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());

			// Tạo ví cho người dùng mới
			Wallet wallet = new Wallet();
			wallet.setUser(user);
			wallet.setBalance(new BigDecimal("10000000")); // Tặng 10tr cho người dùng mới
			wallet.setLastUpdated(timestamp.toString());
			wallet.setCreateDay(timestamp.toString());
			walletService.save(wallet); // Lưu ví người dùng

			// Cấp quyền ROLE_USER
			Role role = roleService.findRoleById(1); // Giả sử roleId = 1 là ROLE_USER
			UserRole userRole = new UserRole();
			userRole.setUser(user);
			userRole.setRole(role);
			userRoleService.save(userRole); // Gán role cho người dùng

			// LẤY HOẶC TẠO MÃ GIẢM GIÁ MẶC ĐỊNH
			Discount discount = discountDao.findById(18).orElse(null); // Giả sử ID = 1 là mã giảm giá mặc định
			if (discount != null) {
				// Gửi email mã giảm giá
				discountService.sendCodeDiscount(discount.getId(), user);
			}
		}

		// Nếu user đã tồn tại, lấy danh sách role hiện có
		Role userRole = userRoleServiceImpl.findRoleByUserId(user.getId()); 
		String roleName = (userRole != null) ? userRole.getName() : "ROLE_USER"; 
		// Cấp quyền ROLE_USER
		GrantedAuthority authority = new SimpleGrantedAuthority(roleName);

		return new DefaultOAuth2User(Collections.singleton(authority), oauth2User.getAttributes(), "email");
	}
}
