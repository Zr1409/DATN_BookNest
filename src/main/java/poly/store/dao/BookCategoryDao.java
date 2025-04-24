package poly.store.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import poly.store.entity.BookCategory;

public interface BookCategoryDao extends JpaRepository<BookCategory, Integer>{
	@Query("SELECT c FROM BookCategory c WHERE c.Deleteday = NULL")
	List<BookCategory> getListBookCategory();

	
	@Query("SELECT c FROM BookCategory c WHERE c.Deleteday = null AND c.Namesearch LIKE ?1")
	BookCategory getBookCategoryByNameSearch(String nameSearch);
}
