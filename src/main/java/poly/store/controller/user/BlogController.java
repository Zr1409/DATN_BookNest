package poly.store.controller.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import poly.store.entity.Blog;
import poly.store.entity.Book;
import poly.store.entity.User;
import poly.store.model.BestSellerModel;
import poly.store.model.ShowBook;
import poly.store.service.BlogService;
import poly.store.service.BookReviewsService;
import poly.store.service.OrderService;
import poly.store.service.ParamService;
import poly.store.service.BookService;
import poly.store.service.UserService;

@Controller
public class BlogController {
	@Autowired
	BlogService blogService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	ParamService paramService;
	
	@Autowired
	BookReviewsService bookReviewsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	BookService bookService;

	@GetMapping("/blog")
	public String index(Model model, @RequestParam("p") Optional<Integer> p) {
		
		Pageable pageable = PageRequest.of(p.orElse(0), 8);
		
		
		Page<Blog> list = blogService.findAllBlogActive(pageable);
		
		for(Blog blog: list) {
			String[] uploadDay = blog.getUploadday().split("-");
			String result = uploadDay[2] + "/" + uploadDay[1] + "/" + uploadDay[0];
			blog.setUploadday(result);		
		}
		model.addAttribute("blogList", list);
		return Constants.USER_DISPLAY_BLOG;
	}

	@GetMapping("/blog/{nameSearch}")
	public String index(Model model, @PathVariable("nameSearch") String nameSearch) {
		Blog blog = blogService.findBlogByNameSearch(nameSearch);
		User user = userService.findById(blog.getPersoncreate());
		String[] uploadDay = blog.getUploadday().split("-");
		String result = uploadDay[2] + "/" + uploadDay[1] + "/" + uploadDay[0];
		blog.setUploadday(result);			
		System.out.println(blog.getUploadday());
		
		model.addAttribute("blogInfor", blog);
		model.addAttribute("blogUser", user);
		return Constants.USER_DISPLAY_BLOG_DETAIL;
	}
	
	@ModelAttribute("listBestSeller")
	public List<ShowBook> getListBestSeller(Model model){
		Pageable topFour = PageRequest.of(0, 4);
		
		List<BestSellerModel> list = orderService.getListBestSellerBook(topFour);
		
		List<ShowBook> listBook = new ArrayList<ShowBook>();
		
		for(BestSellerModel bestSeller: list) {
			ShowBook showBook = new ShowBook();
			int totalStar =  bookReviewsService.getAllStarBookReviewsByBookNameSearch(bestSeller.getBook().getNamesearch());
			showBook.setBook(bestSeller.getBook());
			showBook.setTotalStar(totalStar);
			listBook.add(showBook);
		}		
		return listBook;
	}
	
	@ModelAttribute("listSale")
	public List<ShowBook> getListBookSale(Model model) {
		List<Book> list = bookService.getListBookSales();

		List<ShowBook> listBook = new ArrayList<ShowBook>();

		for (Book book : list) {
			ShowBook showBook = new ShowBook();
			int totalStar =  bookReviewsService.getAllStarBookReviewsByBookNameSearch(book.getNamesearch());
			showBook.setBook(book);
			showBook.setTotalStar(totalStar);
			listBook.add(showBook);
		}

		return listBook;
	}
}
