package poly.store.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.store.entity.SubBookCategory;

public interface SubBookCategoryDao extends JpaRepository<SubBookCategory, Integer>{
	@Query("SELECT m FROM SubBookCategory m WHERE m.Deleteday = null and m.bookCategory.Deleteday = null")
	List<SubBookCategory> getListSubBookCategory();
	
	@Query("SELECT m FROM SubBookCategory m WHERE m.Deleteday = null and m.bookCategory.id = :uid")
	List<SubBookCategory> getListBookCategoryById(@Param("uid") int email);
}
