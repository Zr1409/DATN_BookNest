
package poly.store.service;

import java.util.List;

import poly.store.entity.Role;


public interface RoleService {
	
	/**
	 * Tim role bang role id
	 * 
	 * @param thong tin id cua role
	 * @return Role tim duoc
	 */
	Role findRoleById(int roleId);
	
	/**
	 * Tim ten vai tro dua vao user id
	 * 
	 * @param thong tin user id
	 * @return danh sach ten vai tro tim duoc
	 */
	List<String> getRoleNames(int userId);

}
