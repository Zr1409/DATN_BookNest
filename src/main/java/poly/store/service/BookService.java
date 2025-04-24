
package poly.store.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import poly.store.entity.Book;
import poly.store.model.BookModel;
import poly.store.model.ShowBook;


public interface BookService {

	BookModel createBook(BookModel bookModel);

	List<Book> findAll();

	void delete(Integer id);

	BookModel updateBook(BookModel bookModel);

	BookModel getOneBookById(Integer id);

	List<Book> getListLatestBook();

	List<Book> getListViewsBook();

	Page<Book> getListBookByNameSearch(String nameSearch, Pageable pageable);

	List<Book> getDemo(String nameSearch);

	Page<Book> getListBookByPrice(String nameSearch, int minPrice, int maxPrice, Pageable pageable);

	Page<ShowBook> getListBookByFilter(String nameSearch, String price, String publisher, String sort, Pageable pageable, String status, String name, String subBookCategory);

	Book getBookByNameSearch(String nameSearch);

	List<Book> getListBookRelated(int id);

	void updateView(String nameSearch);

	void updateQuality(Book book);

	List<Book> getListBookSales();
	
	List<ShowBook> getAllShowBooks();
	
	 // Tìm sách theo tên (case insensitive)
	List<Book>  getBookByNameSearchChatBox(String bookName);
	

}
