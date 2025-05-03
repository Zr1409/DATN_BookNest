
package poly.store.service;

import java.util.List;

import poly.store.entity.Role;
import poly.store.entity.User;
import poly.store.entity.UserRole;


public interface UserRoleService{
	
	/**
	 * Luu user role vao database
	 * 
	 * @param userRole
	 */
	void save(UserRole userRole);

	List<UserRole> findAll();

	List<UserRole> findAllAdminOrDirector();

	void delete(Integer id);
	
	Role findRoleByUserId(int userId);
	
}
