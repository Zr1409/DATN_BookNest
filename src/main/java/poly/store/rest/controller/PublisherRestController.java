
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

import poly.store.entity.Publisher;
import poly.store.model.PublisherModel;
import poly.store.service.PublisherService;

/**
 * Class cung cap cac dich vu rest api cho bang publisher
 * 
 * @author 
 * @version 
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/rest/publisher")
public class PublisherRestController {
	@Autowired
	PublisherService publisherService;
	
	@PostMapping("/form")
	public PublisherModel create(@RequestBody PublisherModel publisherModel) {
		return publisherService.createPublisher(publisherModel);
	}
	
	@GetMapping()
	public List<Publisher> getAll(){
		return publisherService.findAll();
	}
	
	@GetMapping("/form/{id}")
	public PublisherModel getOneUserById(@PathVariable("id") Integer id) {
		return publisherService.getOnePublisherById(id);
	}
	
	@DeleteMapping("{id}")
	public void delete(@PathVariable("id") Integer id) {
		publisherService.delete(id);
	}
	
	@PutMapping("/form/{id}")
	public PublisherModel update(@PathVariable("id") Integer id, @RequestBody PublisherModel publisherModel) {
		return publisherService.updatePublisher(publisherModel);
	}
}
