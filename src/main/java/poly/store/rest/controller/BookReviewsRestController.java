
package poly.store.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import poly.store.entity.BookReviews;
import poly.store.model.BookReviewsModel;
import poly.store.service.BookReviewsService;

/**
 * Class cung cap cac dich vu rest api cho bang BookReviews
 * 
 * @author 
 * @version 
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/rest/bookReviews")
public class BookReviewsRestController {
	@Autowired
	BookReviewsService bookReviewsService;
	
	@GetMapping("/form/book/{id}")
	public List<BookReviews> getListBookReviewsByProductId(@PathVariable("id") Integer id) {
		return bookReviewsService.getListBookReviewsByBookId(id);
	}
	
	@PostMapping("/form")
	public BookReviewsModel create(@RequestBody BookReviewsModel bookReviewsModel) {
		return bookReviewsService.createBookReviews(bookReviewsModel);
	}
	
	@GetMapping("/pending")
	public List<BookReviews> getListBookReviewsPending(){
		return bookReviewsService.getListBookReviewsPending();
	}
	
	@GetMapping("/pending/{id}")
	public BookReviews getBookReviewsByBookReviewsId(@PathVariable("id") Integer id) {
		return bookReviewsService.getBookReviewsByBookReviewsId(id);
	}
	
	@PutMapping("/form/approve/{id}")
	public void approve(@PathVariable("id") Integer id) {
		bookReviewsService.approveBookReviews(id);
	}
	
	@DeleteMapping("/form/delete/{id}")
	public void delete(@PathVariable("id") Integer id) {
		bookReviewsService.delete(id);
	}
	
	@GetMapping("/approved")
	public List<BookReviews> getListBookReviewsChecked(){
		return bookReviewsService.getListBookReviewsChecked();
	}
}
