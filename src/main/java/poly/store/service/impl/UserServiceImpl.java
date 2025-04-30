
package poly.store.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import poly.store.dao.UserDao;
import poly.store.entity.User;
import poly.store.model.ChangePassModel;
import poly.store.model.InformationModel;
import poly.store.service.UserService;

/**
 * Class trien khai theo interface UserService, Thao tac voi Class UserDao de
 * thuc hien cac tac vu tuong ung
 */
@Service
public class UserServiceImpl implements UserService {
	// Thong tin class User Dao
	@Autowired
	UserDao userDao;

	@Autowired
	@Lazy
	private PasswordEncoder passwordEncoder;

	/**
	 * Tim user bang email truyen vao
	 * 
	 * @param username thong tin Email
	 * @return User tim duoc
	 */
	@Override
	public User findUserByEmail(String email) {
		// Tra ve User tim duoc
		return userDao.findUserByEmail(email);
	}

	/**
	 * Luu user vao database
	 * 
	 * @param user thong tin user
	 */
	@Override
	public void save(User user) {
		userDao.save(user);
	}

	@Override
	public List<User> findAll() {
		// TODO Auto-generated method stub
		return userDao.findAll();
	}

	@Override
	public User create(User user) {
		return userDao.save(user);
	}

	@Override
	public List<User> findAllUserAnable() {
		return userDao.findAllUserAnable();
	}

	@Override
	public InformationModel getUserAccount() {
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
		User user = userDao.findUserByEmail(username);

		InformationModel information = new InformationModel();

		information.setPassword(user.getPassword());
		information.setFullName(user.getFullname());
		information.setEmail(user.getEmail());
		information.setBirthday(user.getBirthday());
		information.setGender(user.getSex());
		information.setNews(user.getSubscribe());

		return information;
	}

	@Override
	public InformationModel update(InformationModel informationModel) {
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

		User user = userDao.findUserByEmail(username);

		user.setFullname(informationModel.getFullName());
		user.setBirthday(informationModel.getBirthday());
		user.setSubscribe(informationModel.getNews());
		user.setSex(informationModel.getGender());

		userDao.save(user);

		return informationModel;
	}

	public User getCurrentUser() {
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
			return null; 
		}

		return userDao.findUserByEmail(username);
	}

	@Override
	public ChangePassModel updatePass(ChangePassModel changePassModel) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = null;

		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else if (principal instanceof OAuth2User) {
			OAuth2User oauth2User = (OAuth2User) principal;
			username = (String) oauth2User.getAttribute("email");
		}

		if (username == null) {
			throw new IllegalStateException("User not authenticated");
		}

		User user = userDao.findUserByEmail(username);
		if (!passwordEncoder.matches(changePassModel.getOldPass(), user.getPassword())) {
			throw new IllegalArgumentException("Mật khẩu cũ không chính xác");
		}

		if (passwordEncoder.matches(changePassModel.getNewPass(), user.getPassword())) {
			throw new IllegalArgumentException("Mật khẩu mới không được trùng với mật khẩu cũ");
		}

		String encodedNewPassword = passwordEncoder.encode(changePassModel.getNewPass());
		user.setPassword(encodedNewPassword);

		userDao.save(user);

		return changePassModel; // Có thể trả về đối tượng để thông báo thành công hoặc lỗi
	}

	@Override
	public User findById(Integer id) {
		// TODO Auto-generated method stub
		return userDao.findById(id).get();
	}

	@Override
	public InformationModel createUser(InformationModel informationModel) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		User user = new User();

		user.setEmail(informationModel.getEmail());
		user.setFullname(informationModel.getFullName());
		user.setBirthday(informationModel.getBirthday());
		user.setSubscribe(informationModel.getNews());
		user.setSex(informationModel.getGender());
		user.setPassword("1234567");

		user.setCreateday(timestamp.toString());

		userDao.save(user);

		return informationModel;
	}

	@Override
	public List<User> findAllUserNotRoleAdmin() {
		List<User> listUser = userDao.findAllUserNotRoleAdmin();
		return listUser;
	}

	@Override
	public void deleteUser(Integer id) {
		// Xoa user
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

		User temp = userDao.findUserByEmail(username);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		User user = userDao.findById(id).get();

		user.setDeleteday(timestamp.toString());
		user.setPersondelete(temp.getId());
		userDao.save(user);
	}

	@Override
	public InformationModel getOneUserById(Integer id) {
		User user = userDao.findById(id).get();
		InformationModel information = new InformationModel();

		information.setFullName(user.getFullname());
		information.setEmail(user.getEmail());
		information.setGender(user.getSex());
		information.setNews(user.getSubscribe());
		information.setBirthday(user.getBirthday());

		// System.out.println("this is username:" + user.getFullname());

		return information;
	}

	@Override
	public InformationModel updateUser(InformationModel informationModel, Integer id) {

		User user = userDao.findById(id).get();

		// user.setEmail(informationModel.getEmail());
		user.setFullname(informationModel.getFullName());
		user.setBirthday(informationModel.getBirthday());
		user.setSubscribe(informationModel.getNews());
		user.setSex(informationModel.getGender());

		userDao.save(user);

		return informationModel;
	}

	@Override
	public List<User> getListUserEnableSubscribe() {
		return userDao.getListUserEnableSubscribe();
	}
}
