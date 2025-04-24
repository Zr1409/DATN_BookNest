package poly.store.service;

import java.util.List;

import poly.store.entity.Publisher;
import poly.store.model.PublisherModel;

public interface PublisherService{

	PublisherModel createPublisher(PublisherModel publisherrModel);

	List<Publisher> findAll();

	PublisherModel getOnePublisherById(Integer id);

	void delete(Integer id);

	PublisherModel updatePublisher(PublisherModel publisherModel);

}
