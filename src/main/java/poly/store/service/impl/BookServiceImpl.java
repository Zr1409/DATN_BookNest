
package poly.store.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import poly.store.dao.SubBookCategoryDao;
import poly.store.dao.PublisherDao;
import poly.store.dao.BookDao;
import poly.store.dao.UserDao;
import poly.store.entity.SubBookCategory;
import poly.store.entity.Publisher;
import poly.store.entity.Book;
import poly.store.entity.User;
import poly.store.model.BookModel;
import poly.store.model.ShowBook;
import poly.store.service.BookReviewsService;
import poly.store.service.BookService;

/**
 * Class trien khai theo interface UserService, Thao tac voi Class UserDao de
 * thuc hien cac tac vu tuong ung
 */
@Service
public class BookServiceImpl implements BookService {
	@Autowired
	BookDao bookDao;

	@Autowired
	UserDao userDao;

	@Autowired
	PublisherDao publisherDao;

	@Autowired
	SubBookCategoryDao subBookCategoryDao;

	@Autowired
	BookReviewsService bookReviewsService;

	@PersistenceContext
	private EntityManager em;

	@Override
	public BookModel createBook(BookModel bookModel) {
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

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		User temp = userDao.findUserByEmail(username);

		Book book = new Book();
		book.setCode(bookModel.getCode());
		book.setName(bookModel.getName());
		book.setAuthor(bookModel.getAuthor());
		book.setPrice(bookModel.getPrice());
		book.setQuantity(bookModel.getQuantity());
		book.setDescription(bookModel.getDescription());
		book.setPublicationYear(bookModel.getPublicationYear());		
		book.setImage1(bookModel.getImage1());
		book.setImage2(bookModel.getImage2());
		book.setImage3(bookModel.getImage3());
		book.setImage4(bookModel.getImage4());
		book.setImage5(bookModel.getImage5());
		book.setActive(bookModel.isActive());
		book.setWeight(bookModel.getWeight());
		book.setDimensions(bookModel.getDimensions());
		book.setPageCount(bookModel.getPageCount());
		book.setFormat(bookModel.getFormat());		
		book.setNamesearch(bookModel.getNameSearch());
		book.setCreateday(timestamp.toString());
		book.setPersoncreate(temp.getId());
		book.setSales(bookModel.getSales());

		Publisher publisher = publisherDao.findById(bookModel.getPublisherId()).get();
		SubBookCategory subBookCategory = subBookCategoryDao.findById(bookModel.getSubBookCategoryId()).get();

		book.setSubBookCategory(subBookCategory);
		book.setPublisher(publisher);

		bookDao.save(book);

		return bookModel;
	}

	@Override
	public List<Book> findAll() {
		return bookDao.getListBook();
	}
	
	@Override
	public List<ShowBook> getAllShowBooks() {
		return bookDao.getAllShowBooks();
	}

	@Override
	public void delete(Integer id) {
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

		Book book = bookDao.findById(id).get();
		book.setDeleteday(timestamp.toString());
		book.setPersondelete(temp.getId());
		bookDao.save(book);
	}

	@Override
	public BookModel updateBook(BookModel bookModel) {
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

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		User temp = userDao.findUserByEmail(username);
		
		Book book = bookDao.findById(bookModel.getId()).get();
		book.setCode(bookModel.getCode());
		book.setName(bookModel.getName());
		book.setAuthor(bookModel.getAuthor());
		book.setPrice(bookModel.getPrice());
		book.setQuantity(bookModel.getQuantity());
		book.setPublicationYear(bookModel.getPublicationYear());
		book.setDescription(bookModel.getDescription());	
		book.setImage1(bookModel.getImage1());
		book.setImage2(bookModel.getImage2());
		book.setImage3(bookModel.getImage3());
		book.setImage4(bookModel.getImage4());
		book.setImage5(bookModel.getImage5());
		book.setActive(bookModel.isActive());
		book.setWeight(bookModel.getWeight());
		book.setDimensions(bookModel.getDimensions());
		book.setPageCount(bookModel.getPageCount());
		book.setFormat(bookModel.getFormat());		
		book.setNamesearch(bookModel.getNameSearch());
		book.setUpdateday(timestamp.toString());
		book.setPersonupdate(temp.getId());
		book.setSales(bookModel.getSales());

		Publisher publisher = publisherDao.findById(bookModel.getPublisherId()).get();
		SubBookCategory subBookCategory = subBookCategoryDao.findById(bookModel.getSubBookCategoryId()).get();

		book.setSubBookCategory(subBookCategory);
		book.setPublisher(publisher);		
		bookDao.save(book);
		return bookModel;
	}

	@Override
	public BookModel getOneBookById(Integer id) {
		Book book = bookDao.findById(id).get();
		BookModel bookModel = new BookModel();
		bookModel.setId(book.getId());
		bookModel.setCode(book.getCode());
		bookModel.setName(book.getName());
		bookModel.setAuthor(book.getAuthor());
		bookModel.setPrice(book.getPrice());
		bookModel.setPublicationYear(book.getPublicationYear());
		bookModel.setQuantity(book.getQuantity());
		bookModel.setImage1(book.getImage1());
		bookModel.setImage2(book.getImage2());
		bookModel.setImage3(book.getImage3());
		bookModel.setImage4(book.getImage4());
		bookModel.setImage5(book.getImage5());
		bookModel.setNameSearch(book.getNamesearch());
		bookModel.setActive(book.isActive());
		bookModel.setWeight(book.getWeight());
		bookModel.setDimensions(book.getDimensions());
		bookModel.setPageCount(book.getPageCount());
		bookModel.setFormat(book.getFormat());		
		bookModel.setPublisherId(book.getPublisher().getId());
		bookModel.setSubBookCategoryId(book.getSubBookCategory().getId());
		bookModel.setDescription(book.getDescription());
		bookModel.setSales(book.getSales());
		return bookModel;
	}

	@Override
	public List<Book> getListLatestBook() {
		return bookDao.getListLatestBook();
	}

	@Override
	public List<Book> getListViewsBook() {
		return bookDao.getListViewsBook();
	}

	@Override
	public Page<Book> getListBookByNameSearch(String nameSearch, Pageable pageable) {
		return bookDao.getListBookByNameSearch(nameSearch, pageable);
	}

	@Override
	public List<Book> getDemo(String nameSearch) {
		// TODO Auto-generated method stub
		return bookDao.getListDemo(nameSearch);
	}

	@Override
	public Page<Book> getListBookByPrice(String nameSearch, int minPrice, int maxPrice, Pageable pageable) {
		// TODO Auto-generated method stub
		return bookDao.getListBookByPrice(nameSearch, minPrice, maxPrice, pageable);
	}

	@Override
	public Page<ShowBook> getListBookByFilter(String nameSearch, String price, String publisher, String sort,
			Pageable pageable, String status, String name, String bookCategory) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Book> cq = cb.createQuery(Book.class);
		Root<Book> from = cq.from(Book.class);

		Predicate preStatus;

		if (status.equals("danh-sach")) {
			preStatus = cb.like(from.get("subBookCategory").get("bookCategory").get("Namesearch"), "%" + nameSearch + "%");
		} else if (status.equals("tim-kiem")) {
			Predicate preCategory = null;
			if (name.length() != 0) {
				name = "%" + name + "%";
			}
			preStatus = cb.like(from.get("name"), name);
			if (bookCategory.length() != 0) {
				preCategory = cb.like(from.get("subBookCategory").get("bookCategory").get("Namesearch"), "%" + bookCategory + "%");
				preStatus = cb.and(preStatus, preCategory);
			}
		} else {
			preStatus = cb.notEqual(from.get("sales"), 0);
		}

		Predicate preActive = cb.equal(from.get("active"), 1);
		// Predicate preActive = cb.equal(from.get("active"), 1);
		Predicate preDeleteDay = cb.isNull(from.get("Deleteday"));

		int check = 0;
		Predicate prePrice = null;
		Predicate preManu = null;

		if (price != null) {
			int min = 0;
			int max = 999999999;
			if (price.equals("1")) {
				max = 150000;
			} else if (price.equals("2")) {
				min = 150000;
				max = 300000;
			} else if (price.equals("3")) {
				min = 300000;
				max = 500000;
			} else if (price.equals("4")) {
				min = 500000;
				max = 700000;
			} else {
				min = 700000;
			}
			check = 1;
			prePrice = cb.between(from.get("price"), min, max);
		}

		if (publisher != null) {
			preManu = cb.equal(from.get("publisher").get("id"), Integer.parseInt(publisher));
			if (check == 1) {
				check = 3;
			} else {
				check = 2;
			}
		}

		if (check == 1) {
			cq.where(prePrice, preActive, preDeleteDay, preStatus);
		} else if (check == 2) {
			cq.where(preManu, preActive, preDeleteDay, preStatus);
		} else if (check == 3) {
			cq.where(prePrice, preManu, preActive, preDeleteDay, preStatus);
		} else {
			cq.where(preActive, preDeleteDay, preStatus);
		}

		if (sort != null) {
			if (sort.equals("0")) {
				cq.orderBy(cb.desc(from.get("Createday")));
			}
			if (sort.equals("1")) {
				cq.orderBy(cb.asc(from.get("name")));
			}
			if (sort.equals("2")) {
				cq.orderBy(cb.desc(from.get("name")));
			}
			if (sort.equals("3")) {
				cq.orderBy(cb.asc(from.get("price")));
			}
			if (sort.equals("4")) {
				cq.orderBy(cb.desc(from.get("price")));
			}
		} else {
			cq.orderBy(cb.desc(from.get("Createday")));
		}

		TypedQuery<Book> q = em.createQuery(cq);

		List<Book> countAllItems = q.getResultList();

		q.setFirstResult(Math.toIntExact(pageable.getOffset()));
		q.setMaxResults(pageable.getPageSize());

		List<Book> getAllItems = q.getResultList();

		List<ShowBook> listbook = new ArrayList<ShowBook>();

		for (Book book : getAllItems) {
			ShowBook showbook = new ShowBook();
			int totalStar = bookReviewsService.getAllStarBookReviewsByBookNameSearch(book.getNamesearch());
			showbook.setBook(book);
			showbook.setTotalStar(totalStar);
			listbook.add(showbook);
		}

		Page<ShowBook> page = new PageImpl<ShowBook>(listbook, pageable, countAllItems.size());

		return page;
	}

	@Override
	public Book getBookByNameSearch(String nameSearch) {
		return bookDao.getBookByNameSearch(nameSearch);
	}

	@Override
	public List<Book> getListBookRelated(int id) {
		return bookDao.getListBookRelated(id);
	}

	@Override
	public void updateView(String nameSearch) {
		Book book = bookDao.getBookByNameSearch(nameSearch);
		int view = book.getViews();
		book.setViews(view + 1);
		bookDao.save(book);
	}

	@Override
	public void updateQuality(Book book) {
		bookDao.save(book);
	}

	@Override
	public List<Book> getListBookSales() {
		return bookDao.getListBookSales();
	}
	
	 // Tìm sách theo tên (case insensitive)
	 public  List<Book> getBookByNameSearchChatBox(String bookName) {
	        // Sử dụng phương thức tìm kiếm từ repository
		 return bookDao.findByNameContainingIgnoreCase(bookName);
	           // Nếu tìm thấy trả về book, nếu không trả về null
	    }
	
	

}
