package poly.store.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import poly.store.dao.BookCategoryDao;
import poly.store.dao.SubBookCategoryDao;
import poly.store.dao.UserDao;
import poly.store.entity.BookCategory;
import poly.store.entity.SubBookCategory;
import poly.store.entity.User;
import poly.store.model.SubBookCategoryModel;
import poly.store.service.SubBookCategoryService;

@Service
public class SubBookCategoryServiceImpl implements SubBookCategoryService{
	@Autowired
	SubBookCategoryDao subBookCategoryDao;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	BookCategoryDao bookCategoryDao;
	
	@Override
	public SubBookCategoryModel createSubBookCategory(SubBookCategoryModel subBookCategoryModel) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		User temp = userDao.findUserByEmail(username);
		
		BookCategory category = bookCategoryDao.findById(subBookCategoryModel.getBookCategoryId()).get();
		
		SubBookCategory menuOne = new SubBookCategory();
		menuOne.setName(subBookCategoryModel.getName());
		menuOne.setNamesearch(subBookCategoryModel.getNameSearch());
		menuOne.setBookCategory(category);
		menuOne.setCreateday(timestamp.toString());
		menuOne.setPersoncreate(temp.getId());
		
		subBookCategoryDao.save(menuOne);
		return subBookCategoryModel;
	}

	@Override
	public List<SubBookCategory> findAll() {
		return subBookCategoryDao.getListSubBookCategory();
	}

	@Override
	public void delete(Integer id) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails)principal).getUsername();
		User temp = userDao.findUserByEmail(username);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		SubBookCategory entity = subBookCategoryDao.findById(id).get();
		entity.setDeleteday(timestamp.toString());
		entity.setPersondelete(temp.getId());
		subBookCategoryDao.save(entity);
	}

	@Override
	public SubBookCategoryModel getSubBookCategoryById(Integer id) {
		SubBookCategoryModel subBookCategoryModel = new SubBookCategoryModel();
		SubBookCategory entity = subBookCategoryDao.findById(id).get();
		subBookCategoryModel.setName(entity.getName());
		subBookCategoryModel.setNameSearch(entity.getNamesearch());
		subBookCategoryModel.setBookCategoryId(entity.getBookCategory().getId());
		return subBookCategoryModel;
	}

	@Override
	public SubBookCategoryModel updateSubBookCategory(SubBookCategoryModel subBookCategoryModel) {
		BookCategory category = bookCategoryDao.findById(subBookCategoryModel.getBookCategoryId()).get();
		
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		User temp = userDao.findUserByEmail(username);
		
		SubBookCategory entity = subBookCategoryDao.findById(subBookCategoryModel.getId()).get();
		entity.setName(subBookCategoryModel.getName());
		entity.setNamesearch(subBookCategoryModel.getNameSearch());
		entity.setBookCategory(category);
		entity.setUpdateday(timestamp.toString());
		entity.setPersonupdate(temp.getId());
		subBookCategoryDao.save(entity);
		return subBookCategoryModel;
	}

}
