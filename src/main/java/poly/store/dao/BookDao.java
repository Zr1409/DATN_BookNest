
package poly.store.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import poly.store.entity.Book;
import poly.store.model.ShowBook;
import poly.store.model.StatisticalBookDay;

public interface BookDao extends JpaRepository<Book, Integer>{
	@Query("SELECT p FROM Book p WHERE p.Deleteday = null")
	List<Book> getListBook();
	
	@Query(value="SELECT TOP(16) * FROM Books WHERE DeleteDay is NULL and Active = 1 ORDER BY CreateDay DESC", nativeQuery = true)
	List<Book> getListLatestBook();
	
	@Query(value="SELECT TOP(14) * FROM Books WHERE DeleteDay is NULL and Active = 1 ORDER BY Views DESC", nativeQuery = true)
	List<Book> getListViewsBook();
	
	@Query("SELECT p FROM Book p WHERE p.subBookCategory.Namesearch LIKE ?1 AND p.Deleteday = null AND p.active = 1 ORDER BY p.Createday DESC")
	Page<Book> getListBookByNameSearch(String nameSearch, Pageable pageable);
	
	@Query("SELECT p FROM Book p WHERE p.subBookCategory.Namesearch LIKE ?1 AND p.Deleteday = null AND p.active = 1 AND p.price >= ?2 AND p.price <= ?3 ORDER BY p.Createday DESC")
	Page<Book> getListBookByPrice(String nameSearch, int minPrice, int maxPrice, Pageable pageable);
	
	@Query("SELECT p FROM Book p WHERE p.subBookCategory.Namesearch LIKE ?1 AND p.Deleteday = null")
	List<Book> getListDemo(String nameSearch);
	
	@Query("SELECT p FROM Book p WHERE p.Deleteday = null AND p.Namesearch LIKE ?1")
	Book getBookByNameSearch(String nameSearch);
	
	@Query(value="SELECT TOP(10) * FROM Books WHERE DeleteDay is NULL and Active = 1 and SubBookCategory_Id = ?1 ORDER BY Views DESC", nativeQuery = true)
	List<Book> getListBookRelated(int manuId);
	
	@Query(value="SELECT TOP(5) * FROM Books WHERE DeleteDay is NULL and Active = 1 and Sales != 0 ORDER BY Views DESC", nativeQuery = true)
	List<Book> getListBookSales();
	
	@Query(value="SELECT * FROM Books WHERE NOT EXISTS (SELECT * FROM ORDERS WHERE Books.Id = ORDERS.Book_Id) AND Books.DeleteDay is NULL", nativeQuery = true)
	List<Book> listStatisticalBookWarehouse();
	
	//So luong san pham da ban
	@Query("SELECT new poly.store.model.ShowBook(b, COALESCE(SUM(od.quantity), 0)) " +
		       "FROM poly.store.entity.Book b " +
		       "LEFT JOIN poly.store.entity.OrderDetail od ON od.book.id = b.id " +  // Liên kết với OrderDetail
		       "LEFT JOIN od.order o ON o.id = od.order.id " +  // Liên kết với Order để truy vấn theo trạng thái
		       "WHERE o.status = 2 " +  // Trạng thái thành công
		       "GROUP BY b.id")
		List<ShowBook> getAllShowBooks();
	
	 // Tìm sách theo tên (case insensitive)
	 List<Book> findByNameContainingIgnoreCase(String name);


		

}
