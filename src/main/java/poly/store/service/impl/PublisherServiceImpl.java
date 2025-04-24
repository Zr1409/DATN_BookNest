package poly.store.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import poly.store.dao.PublisherDao;
import poly.store.dao.UserDao;
import poly.store.entity.Publisher;
import poly.store.entity.User;
import poly.store.model.PublisherModel;
import poly.store.service.PublisherService;

@Service
public class PublisherServiceImpl implements PublisherService{
	@Autowired
	PublisherDao publisherDao;
	
	@Autowired
	UserDao userDao;
	
	@Override
	public PublisherModel createPublisher(PublisherModel publisherModel) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		User temp = userDao.findUserByEmail(username);
		
		Publisher Publisher = new Publisher();
		Publisher.setName(publisherModel.getName());
		Publisher.setLogo(publisherModel.getLogo());
		Publisher.setBanner(publisherModel.getBanner());
		Publisher.setDescription(publisherModel.getDescribe());
		Publisher.setPersoncreate(temp.getId());
		Publisher.setCreateday(timestamp.toString());
		publisherDao.save(Publisher);
		return publisherModel;
	}

	@Override
	public List<Publisher> findAll() {
		return publisherDao.getListPublisher();
	}

	@Override
	public PublisherModel getOnePublisherById(Integer id) {
		Publisher Publisher = publisherDao.findById(id).get();
		PublisherModel PublisherModel = new PublisherModel();
		PublisherModel.setName(Publisher.getName());
		PublisherModel.setLogo(Publisher.getLogo());
		PublisherModel.setBanner(Publisher.getBanner());
		PublisherModel.setDescribe(Publisher.getDescription());
		return PublisherModel;
	}

	@Override
	public void delete(Integer id) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails)principal).getUsername();
		User temp = userDao.findUserByEmail(username);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		Publisher publisher = publisherDao.findById(id).get();
		publisher.setPersondelete(temp.getId());
		publisher.setDeleteday(timestamp.toString());
		publisherDao.save(publisher);
	}

	@Override
	public PublisherModel updatePublisher(PublisherModel publisherModel) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		User temp = userDao.findUserByEmail(username);
		
		Publisher publisher = publisherDao.findById(publisherModel.getId()).get();
		publisher.setName(publisherModel.getName());
		publisher.setLogo(publisherModel.getLogo());
		publisher.setBanner(publisherModel.getBanner());
		publisher.setDescription(publisherModel.getDescribe());
		publisher.setUpdateday(timestamp.toString());
		publisher.setPersonupdate(temp.getId());
		publisherDao.save(publisher);
		return publisherModel;
	}

}
