
package poly.store.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import poly.store.dao.EmployeeDao;
import poly.store.dao.UserDao;
import poly.store.dao.UserRoleDao;
import poly.store.entity.Employee;
import poly.store.entity.Role;
import poly.store.entity.User;
import poly.store.entity.UserRole;
import poly.store.service.UserRoleService;

/**
 * Class trien khai theo interface UserRoleService, Thao tac voi Class
 * UserRoleDao de thuc hien cac tac vu tuong ung
 * 
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {
	// Thong tin user role dao
	@Autowired
	UserRoleDao userRoleDao;

	@Autowired
	UserDao userDao;

	@Autowired
	EmployeeDao employeeDao;

	/**
	 * Luu user role vao database
	 * 
	 * @param thong tin user role
	 */
	@Override
	public void save(UserRole userRole) {
		userRoleDao.save(userRole);
	}

	/**
	 * Tim kiem tat ca entity trong user role
	 * 
	 * @param thong tin user role
	 */
	@Override
	public List<UserRole> findAll() {
		return userRoleDao.findAll();
	}

	/**
	 * Tim kiem tat cac entity trong user role co role la admin hoac director
	 */
	@Override
	public List<UserRole> findAllAdminOrDirector() {
		return userRoleDao.findAllAdminOrDirector();
	}

	@Override
	public void delete(Integer id) {
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
		
		if(!temp.getEmail().equals(user.getEmail())) {
			user.setDeleteday(timestamp.toString());
			user.setPersondelete(temp.getId());
			userDao.save(user);
			
			// Xoa employee
			List<Employee> listEmployee = user.getListEmployee();
			for(Employee e: listEmployee) {
				e.setDeleteday(timestamp.toString());
				e.setPersondelete(temp.getId());
				employeeDao.save(e);
			}
		}	
		
		else {
			throw new RuntimeException();
		}
	}
	
	@Override
	public Role findRoleByUserId(int userId) {
		return userRoleDao.findRoleByUserId(userId);
	}

}
