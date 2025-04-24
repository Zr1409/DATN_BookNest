
package poly.store.controller.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import poly.store.common.Constants;
import poly.store.entity.BookCategory;
import poly.store.entity.Publisher;
import poly.store.model.BestSellerModel;
import poly.store.model.ShowBook;
import poly.store.service.BookCategoryService;
import poly.store.service.BookReviewsService;
import poly.store.service.PublisherService;
import poly.store.service.OrderService;
import poly.store.service.BookService;
import poly.store.service.SessionService;

/**
 * Class de danh sach san pham
 * 
 * @author 
 * @version 
 */
@Controller
public class ListBookController {
	@Autowired
	BookService bookService;

	@Autowired
	BookCategoryService bookCategoryService;

	@Autowired
	SessionService sessionService;

	@Autowired
	PublisherService publisherService;

	@Autowired
	OrderService orderService;

	@Autowired
	BookReviewsService bookReviewsService;

	@GetMapping("/danh-sach/{nameSearch}")
	public String index(@PathVariable("nameSearch") String nameSearch, Model model,
			@RequestParam("p") Optional<Integer> p, @RequestParam(name = "gia", required = false) String price,
			@RequestParam(name = "hang", required = false) String publisher,
			@RequestParam(name = "xep", required = false) String sort) {
		try {
			Pageable pageable = PageRequest.of(p.orElse(0), 15);

			Page<ShowBook> listBook = bookService.getListBookByFilter(nameSearch, price, publisher, sort,
					pageable, "danh-sach", "", "");
			 // Lấy danh sách số lượng đã bán
	        List<ShowBook> soldQuantityList =  bookService.getAllShowBooks();

	        // Chuyển danh sách số lượng đã bán thành Map để tra cứu nhanh
	        Map<Integer, Long> soldQuantityMap = soldQuantityList.stream()
	                .collect(Collectors.toMap(sp -> sp.getBook().getId(), ShowBook::getSoldQuantity));

	        // Cập nhật số lượng đã bán cho từng sản phẩm trong listBook
	        listBook.forEach(sp -> {
	            Long soldQuantity = soldQuantityMap.getOrDefault(
	                    sp.getBook() != null ? sp.getBook().getId() : null, 0L
	            );
	            sp.setSoldQuantity(soldQuantity);
	        });
			model.addAttribute("listBook", listBook);
			model.addAttribute("price", price);
			model.addAttribute("publisher", publisher);
			model.addAttribute("sort", sort);
			model.addAttribute("nameSearch", nameSearch);

			BookCategory bookCategory = bookCategoryService.getBookCategoryByNameSearch(nameSearch);
			model.addAttribute("inforCategory", bookCategory);
		} catch (Exception e) {
			return "redirect:/index";
		}

		return Constants.USER_DISPLAY_LIST_BOOK_BY_CATEGORY;
	}

	@GetMapping("/uu-dai")
	public String sales(Model model, @RequestParam("p") Optional<Integer> p,
			@RequestParam(name = "gia", required = false) String price,
			@RequestParam(name = "hang", required = false) String publisher,
			@RequestParam(name = "xep", required = false) String sort) {

		Pageable pageable = PageRequest.of(p.orElse(0), 15);

		Page<ShowBook> listBook =  bookService.getListBookByFilter("", price, publisher, sort, pageable, "uu-dai",
				"", "");
		// Lấy danh sách số lượng đã bán
	    List<ShowBook> soldQuantityList = bookService.getAllShowBooks();

	    // Chuyển danh sách số lượng đã bán thành Map để tra cứu nhanh
	    Map<Integer, Long> soldQuantityMap = soldQuantityList.stream()
	            .collect(Collectors.toMap(sp -> sp.getBook().getId(), ShowBook::getSoldQuantity));

	    // Cập nhật số lượng đã bán cho từng sản phẩm trong listProduct
	    listBook.forEach(sp -> {
	        Long soldQuantity = soldQuantityMap.getOrDefault(
	                sp.getBook() != null ? sp.getBook().getId() : null, 0L
	        );
	        sp.setSoldQuantity(soldQuantity);
	    });
		model.addAttribute("listBook", listBook);
		model.addAttribute("price", price);
		model.addAttribute("publisher", publisher);
		model.addAttribute("sort", sort);

		return Constants.USER_DISPLAY_LIST_BOOK_BY_SALES;
	}

	@GetMapping("/tim-kiem")
	public String searcch(Model model, @RequestParam(name = "q", required = false) String name,
			@RequestParam("p") Optional<Integer> p, @RequestParam(name = "gia", required = false) String price,
			@RequestParam(name = "hang", required = false) String publisher,
			@RequestParam(name = "xep", required = false) String sort,
			@RequestParam(name = "category", required = false) String bookCategory) {
		try {
			Pageable pageable = PageRequest.of(p.orElse(0), 15);

			if (bookCategory == null) {
				bookCategory = "";
			}

			if (name == null) {
				name = "";
			}

			Page<ShowBook> listBook =  bookService.getListBookByFilter("", price, publisher, sort, pageable,
					"tim-kiem", name.trim(), bookCategory);
			if (!name.trim().isEmpty()) {
				model.addAttribute("title", "- " + name.trim());
				model.addAttribute("name", name.trim());
			}
			model.addAttribute("bookCategory", bookCategory);
			model.addAttribute("listBook", listBook);
			model.addAttribute("price", price);
			model.addAttribute("publisher", publisher);
			model.addAttribute("sort", sort);

			List<BookCategory> listBookCategory = bookCategoryService.findAll();
			model.addAttribute("listBookCategory", listBookCategory);

		} catch (Exception e) {
			return "redirect:/index";
		}

		return Constants.USER_DISPLAY_LIST_BOOK_BY_SEARCH;
	}

	@ModelAttribute("listPublisher")
	public List<Publisher> listPublisher() {
		List<Publisher> list = publisherService.findAll();
		return list;
	}

	@ModelAttribute("listBestSeller")
	public List<ShowBook> getListBestSeller(Model model) {
		Pageable topFour = PageRequest.of(0, 4);

		List<BestSellerModel> list = orderService.getListBestSellerBook(topFour);

		List<ShowBook> listBook = new ArrayList<ShowBook>();

		for (BestSellerModel bestSeller : list) {
			ShowBook showProduct = new ShowBook();
			int totalStar = bookReviewsService
					.getAllStarBookReviewsByBookNameSearch(bestSeller.getBook().getNamesearch());
			showProduct.setBook(bestSeller.getBook());
			showProduct.setTotalStar(totalStar);
			listBook.add(showProduct);
		}
		return listBook;
	}
}
