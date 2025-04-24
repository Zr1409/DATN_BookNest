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

import poly.store.entity.SubBookCategory;
import poly.store.model.SubBookCategoryModel;
import poly.store.service.SubBookCategoryService;

@CrossOrigin("*")
@RestController
@RequestMapping("/rest/subBookCategory")
public class SubBookCategoryRestController {
	@Autowired
	SubBookCategoryService subBookCategoryService;
	
	@PostMapping("/form")
	public SubBookCategoryModel create(@RequestBody SubBookCategoryModel SubBookCategoryModel) {
		return subBookCategoryService.createSubBookCategory(SubBookCategoryModel);
	}
	
	@GetMapping()
	public List<SubBookCategory> getAll(){
		return subBookCategoryService.findAll();
	}
	
	@DeleteMapping("{id}")
	public void delete(@PathVariable("id") Integer id) {
		subBookCategoryService.delete(id);
	}
	
	@GetMapping("/form/{id}")
	public SubBookCategoryModel getSubBookCategoryById(@PathVariable("id") Integer id) {
		return subBookCategoryService.getSubBookCategoryById(id);
	}
	
	@PutMapping("/form/{id}")
	public SubBookCategoryModel update(@PathVariable("id") Integer id, @RequestBody SubBookCategoryModel SubBookCategoryModel) {
		return subBookCategoryService.updateSubBookCategory(SubBookCategoryModel);
	}
}
