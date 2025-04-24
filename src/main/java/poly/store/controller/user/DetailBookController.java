package poly.store.controller.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import poly.store.common.Constants;
import poly.store.entity.Book;
import poly.store.model.BestSellerModel;
import poly.store.model.CartModel;
import poly.store.model.ShowBook;
import poly.store.service.BookCategoryService;
import poly.store.service.BookReviewsService;
import poly.store.service.OrderService;
import poly.store.service.BookService;
import poly.store.service.SessionService;
import poly.store.service.impl.ShoppingCartServiceImpl;

@Controller
public class DetailBookController {
	@Autowired
	BookService bookService;

	@Autowired
	BookCategoryService categoryService;

	@Autowired
	SessionService sessionService;

	@Autowired
	BookReviewsService bookReviewsService;

	@Autowired
	ShoppingCartServiceImpl shoppingCartServiceImpl;

	@Autowired
	OrderService orderService;

	@GetMapping("/san-pham/{nameSearch}")
	public String index(@PathVariable("nameSearch") String nameSearch, Model model) {
		bookService.updateView(nameSearch);
		model.addAttribute("infor", false);
		sessionService.set("sessionProduct", shoppingCartServiceImpl);
		return Constants.USER_DISPLAY_DETAIL_BOOK;
	}

	// @SuppressWarnings("static-access")
	@PostMapping("/san-pham/{nameSearch}")
	public String orderProduct(@PathVariable("nameSearch") String nameSearch, Model model, HttpServletRequest req) {

		Book book = bookService.getBookByNameSearch(nameSearch);

//		Map<Integer, CartModel> map = ShoppingCartServiceImpl.map;
//		CartModel cartModel = map.get(book.getId());
		// Lấy sản phẩm từ giỏ thông qua service
		CartModel cartModel = shoppingCartServiceImpl.getItems().stream().filter(item -> item.getId() == book.getId())
				.findFirst().orElse(null);

		if (cartModel == null) {
			cartModel = new CartModel();
			cartModel.setId(book.getId());
			cartModel.setBook(book);
			cartModel.setQuantity(1);
			shoppingCartServiceImpl.add(book.getId(), cartModel);
		}

		else {
			shoppingCartServiceImpl.update(cartModel.getId(), cartModel.getQuantity() + 1);
		}

		model.addAttribute("infor", true);

		sessionService.set("sessionProduct", shoppingCartServiceImpl);

		return Constants.USER_DISPLAY_DETAIL_BOOK;
	}

	@ModelAttribute("inforProduct")
	public Book inforCategory(@PathVariable("nameSearch") String nameSearch) {
		Book product = bookService.getBookByNameSearch(nameSearch);
		return product;
	}

	@ModelAttribute("listBookRelated")
	public List<ShowBook> listProductRelated(@PathVariable("nameSearch") String nameSearch) {
		Book product = bookService.getBookByNameSearch(nameSearch);
		List<Book> list = bookService.getListBookRelated(product.getSubBookCategory().getId());
		// Lấy danh sách số lượng đã bán
		List<ShowBook> soldQuantityList = bookService.getAllShowBooks();

		// Chuyển danh sách số lượng đã bán thành Map để tra cứu nhanh
		Map<Integer, Long> soldQuantityMap = soldQuantityList.stream()
				.collect(Collectors.toMap(sp -> sp.getBook().getId(), ShowBook::getSoldQuantity));
		List<ShowBook> listProduct = new ArrayList<ShowBook>();

		for (Book item : list) {
			ShowBook showProduct = new ShowBook();
			int totalStar = bookReviewsService.getAllStarBookReviewsByBookNameSearch(item.getNamesearch());
			showProduct.setBook(item);
			showProduct.setTotalStar(totalStar);
			// Gán số lượng đã bán vào product
			Long soldQuantity = soldQuantityMap.getOrDefault(item.getId(), 0L);
			showProduct.setSoldQuantity(soldQuantity);
			listProduct.add(showProduct);
		}

		return listProduct;
	}

	@ModelAttribute("countBookReviews")
	public int countComment(@PathVariable("nameSearch") String nameSearch) {
		int result = bookReviewsService.getCountBookReviewsByBookNameSearch(nameSearch);
		return result;
	}

	@ModelAttribute("totalStar")
	public int totalStar(@PathVariable("nameSearch") String nameSearch) {
		int result = bookReviewsService.getAllStarBookReviewsByBookNameSearch(nameSearch);
		return result;
	}

	@ModelAttribute("listBestSeller")
	public List<ShowBook> getListBestSeller(Model model) {
		Pageable topFour = PageRequest.of(0, 4);

		List<BestSellerModel> list = orderService.getListBestSellerBook(topFour);

		List<ShowBook> listProduct = new ArrayList<ShowBook>();

		for (BestSellerModel bestSeller : list) {
			ShowBook showBook = new ShowBook();
			int totalStar = bookReviewsService
					.getAllStarBookReviewsByBookNameSearch(bestSeller.getBook().getNamesearch());
			showBook.setBook(bestSeller.getBook());
			showBook.setTotalStar(totalStar);
			listProduct.add(showBook);
		}
		return listProduct;
	}
}
