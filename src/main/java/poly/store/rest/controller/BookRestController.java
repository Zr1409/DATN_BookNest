
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

import poly.store.entity.Book;
import poly.store.model.BookModel;
import poly.store.service.BookService;

/**
 * Class cung cap cac dich vu rest api cho bang book
 * 
 * @author 
 * @version 
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/rest/book")
public class BookRestController {
	@Autowired
	BookService bookService;
	
	@PostMapping("/form")
	public BookModel create(@RequestBody BookModel bookModel) {
		return bookService.createBook(bookModel);
	}
	
	@GetMapping()
	public List<Book> getAll(){
		return bookService.findAll();
	}
	
	@DeleteMapping("{id}")
	public void delete(@PathVariable("id") Integer id) {
		bookService.delete(id);
	}
	
	@PutMapping("/form/{id}")
	public BookModel update(@PathVariable("id") Integer id, @RequestBody BookModel bookModel) {
		return bookService.updateBook(bookModel);
	}
	
	@GetMapping("/form/{id}")
	public BookModel getOnebookById(@PathVariable("id") Integer id) {
		return bookService.getOneBookById(id);
	}
}
