
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

import poly.store.entity.BookCategory;
import poly.store.model.BookCategoryModel;
import poly.store.service.BookCategoryService;

/**
 * Class cung cap cac dich vu rest api cho bang employee
 * 
 * @author 
 * @version 
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/rest/bookCategories")
public class BookCategoryRestController {
	@Autowired
	BookCategoryService categoryService;
	
	@PostMapping("/form")
	public BookCategoryModel create(@RequestBody BookCategoryModel bookCategoryModel) {
		return categoryService.createBookCategory(bookCategoryModel);
	}
	
	@GetMapping()
	public List<BookCategory> getAll(){
		return categoryService.findAll();
	}
	
	@GetMapping("/form/{id}")
	public BookCategoryModel getOneUserById(@PathVariable("id") Integer id) {
		return categoryService.getOneBookCategoryById(id);
	}
	
	@DeleteMapping("{id}")
	public void delete(@PathVariable("id") Integer id) {
		categoryService.delete(id);
	}
	
	@PutMapping("/form/{id}")
	public BookCategoryModel update(@PathVariable("id") Integer id, @RequestBody BookCategoryModel bookCategoryModel) {
		return categoryService.updateBookCategory(bookCategoryModel);
	}
}
