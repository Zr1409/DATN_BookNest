
package poly.store.controller.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import poly.store.common.Constants;
import poly.store.entity.Blog;
import poly.store.entity.Publisher;
import poly.store.entity.Book;
import poly.store.model.BestSellerModel;

import poly.store.model.ShowBook;
import poly.store.service.BlogService;
import poly.store.service.BookReviewsService;
import poly.store.service.FavoriteService;
import poly.store.service.PublisherService;
import poly.store.service.OrderService;
import poly.store.service.ParamService;
import poly.store.service.BookService;
import poly.store.service.UserRoleService;

/**
 * Class de hien thi trang chu nguoi dung
 * 
 * @author 
 * @version 
 */
@Controller
public class IndexController {
	@Autowired
	UserRoleService userRoleService;

	@Autowired
	BookService bookService;

	@Autowired
	PublisherService publisherService;

	@Autowired
	BookReviewsService commentService;

	@Autowired
	BlogService blogService;

	@Autowired
	ParamService paramService;

	@Autowired
	OrderService orderService;

	@Autowired
	FavoriteService favoriteService;

	/**
	 * Hien thi trang chu cua giao dien nguoi dung
	 * 
	 * @return trang index.html
	 */
	@GetMapping({ "/", "/index" })
	public String index(Model model) {
		return Constants.USER_DISPLAY_INDEX;
	}

	@ModelAttribute("publisher")
	public List<Publisher> manufacture(Model model) {
		List<Publisher> list = publisherService.findAll();
		return list;
	}

	@ModelAttribute("latestBook")
	public List<List<ShowBook>> getLatestProduct(Model model) {
		List<Book> list = bookService.getListLatestBook();
		// Lấy danh sách số lượng đã bán
		List<ShowBook> soldQuantityList = bookService.getAllShowBooks();

		// Chuyển danh sách số lượng đã bán thành Map để tra cứu nhanh
		Map<Integer, Long> soldQuantityMap = soldQuantityList.stream()
				.collect(Collectors.toMap(sp -> sp.getBook().getId(), ShowBook::getSoldQuantity));

		List<ShowBook> temp = new ArrayList<>();

		List<List<ShowBook>> result = new ArrayList<List<ShowBook>>();

		for (int i = 0; i < list.size(); i++) {
			int totalStar = commentService.getAllStarBookReviewsByBookNameSearch(list.get(i).getNamesearch());

			ShowBook showBook = new ShowBook();
			showBook.setBook(list.get(i));
			showBook.setTotalStar(totalStar);
			// Gán số lượng đã bán vào product
			Long soldQuantity = soldQuantityMap.getOrDefault(list.get(i).getId(), 0L);
			showBook.setSoldQuantity(soldQuantity);

			temp.add(showBook);
			if (i % 2 != 0) {
				result.add(temp);
				temp = new ArrayList<>();
			}
			if (i == (list.size() - 1)) {
				if (i % 2 == 0) {
					result.add(temp);
					temp = new ArrayList<>();
				}
			}
		}

		return result;
	}

	@ModelAttribute("viewsBook")
	public List<ShowBook> getViewsProduct(Model model) {
		List<Book> list = bookService.getListViewsBook();
		// Lấy danh sách số lượng đã bán
		List<ShowBook> soldQuantityList = bookService.getAllShowBooks();

		// Chuyển danh sách số lượng đã bán thành Map để tra cứu nhanh
		Map<Integer, Long> soldQuantityMap = soldQuantityList.stream()
				.collect(Collectors.toMap(sp -> sp.getBook().getId(), ShowBook::getSoldQuantity));
		List<ShowBook> listBook = new ArrayList<ShowBook>();

		for (Book book : list) {
			ShowBook showBook = new ShowBook();
			int totalStar = commentService.getAllStarBookReviewsByBookNameSearch(book.getNamesearch());
			showBook.setBook(book);
			showBook.setTotalStar(totalStar);
			// Gán số lượng đã bán vào product
			Long soldQuantity = soldQuantityMap.getOrDefault(book.getId(), 0L);
			showBook.setSoldQuantity(soldQuantity);
			listBook.add(showBook);
		}

		return listBook;
	}

	@ModelAttribute("listBlog")
	public List<Blog> getListBlog(Model model) {
		List<Blog> listBlog = blogService.getSixBlog();
		for (Blog blog : listBlog) {
			String uploadDay = paramService.convertDate(blog.getUploadday());
			blog.setUploadday(uploadDay);
		}
		return listBlog;
	}

	@ModelAttribute("listSale")
	public List<ShowBook> getListProductSale(Model model) {
		List<Book> list = bookService.getListBookSales();
		// Lấy danh sách số lượng đã bán
		List<ShowBook> soldQuantityList = bookService.getAllShowBooks();

		// Chuyển danh sách số lượng đã bán thành Map để tra cứu nhanh
		Map<Integer, Long> soldQuantityMap = soldQuantityList.stream()
				.collect(Collectors.toMap(sp -> sp.getBook().getId(), ShowBook::getSoldQuantity));
		List<ShowBook> listBook = new ArrayList<ShowBook>();

		for (Book book : list) {
			ShowBook showBook = new ShowBook();
			int totalStar = commentService.getAllStarBookReviewsByBookNameSearch(book.getNamesearch());
			showBook.setBook(book);
			showBook.setTotalStar(totalStar);
			// Gán số lượng đã bán vào product
			Long soldQuantity = soldQuantityMap.getOrDefault(book.getId(), 0L);
			showBook.setSoldQuantity(soldQuantity);
			listBook.add(showBook);
		}

		return listBook;
	}

	@ModelAttribute("listBestSeller")
	public List<ShowBook> getListBestSeller(Model model) {
		Pageable topFour = PageRequest.of(0, 4);

		List<BestSellerModel> list = orderService.getListBestSellerBook(topFour);

		List<ShowBook> listProduct = new ArrayList<ShowBook>();

		for (BestSellerModel bestSeller : list) {
			ShowBook showProduct = new ShowBook();
			int totalStar = commentService
					.getAllStarBookReviewsByBookNameSearch(bestSeller.getBook().getNamesearch());
			showProduct.setBook(bestSeller.getBook());
			showProduct.setTotalStar(totalStar);
			listProduct.add(showProduct);
		}
		return listProduct;
	}

	@ModelAttribute("listFavorite")
	public List<ShowBook> demo(Model model) {
		Pageable topFour = PageRequest.of(0, 4);
		List<BestSellerModel> list = favoriteService.getListBestSellerBook(topFour);
		// Lấy danh sách số lượng đã bán
				List<ShowBook> soldQuantityList = bookService.getAllShowBooks();

				// Chuyển danh sách số lượng đã bán thành Map để tra cứu nhanh
				Map<Integer, Long> soldQuantityMap = soldQuantityList.stream()
						.collect(Collectors.toMap(sp -> sp.getBook().getId(), ShowBook::getSoldQuantity));
		List<ShowBook> listBook = new ArrayList<ShowBook>();

		for (BestSellerModel bestSeller : list) {
			ShowBook showBook = new ShowBook();
			int totalStar = commentService
					.getAllStarBookReviewsByBookNameSearch(bestSeller.getBook().getNamesearch());
			showBook.setBook(bestSeller.getBook());
			showBook.setTotalStar(totalStar);
			// Gán số lượng đã bán vào product
			Long soldQuantity = soldQuantityMap.getOrDefault(bestSeller.getBook().getId(), 0L);
			showBook.setSoldQuantity(soldQuantity); // Thêm dòng này
			listBook.add(showBook);
		}
		return listBook;
	}

}
