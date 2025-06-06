package poly.store.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import poly.store.dao.BookCategoryDao;
import poly.store.dao.UserDao;
import poly.store.entity.BookCategory;
import poly.store.entity.User;
import poly.store.model.BookCategoryModel;
import poly.store.service.BookCategoryService;

@Service
public class BookCategoryServiceImpl implements BookCategoryService{
	@Autowired
	BookCategoryDao categoryDao;
	
	@Autowired
	UserDao userDao;
	
	@Override
	public BookCategoryModel createBookCategory(BookCategoryModel bookCategoryModel) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = null;

		// Kiểm tra xem principal có phải là UserDetails hay không
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else if (principal instanceof OAuth2User) {
			// Nếu là OAuth2User (DefaultOAuth2User), lấy username (thường là email)
			OAuth2User oauth2User = (OAuth2User) principal;
			username = (String) oauth2User.getAttribute("email"); // Hoặc sử dụng attribute khác nếu có
		}

		if (username == null) {
			throw new RuntimeException("User not authenticated");
		}
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		User temp = userDao.findUserByEmail(username);
		
		BookCategory category = new BookCategory();
		category.setName(bookCategoryModel.getName());
		category.setNamesearch(bookCategoryModel.getNameSearch());
		category.setLogo(bookCategoryModel.getLogo());
		category.setBanner(bookCategoryModel.getBanner());
		category.setDescription(bookCategoryModel.getDescribe());
		category.setPersoncreate(temp.getId());
		category.setCreateday(timestamp.toString());
		categoryDao.save(category);
		return bookCategoryModel;
	}

	@Override
	public List<BookCategory> findAll() {
		return categoryDao.getListBookCategory();
	}

	@Override
	public void delete(Integer id) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = null;

		// Kiểm tra xem principal có phải là UserDetails hay không
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else if (principal instanceof OAuth2User) {
			// Nếu là OAuth2User (DefaultOAuth2User), lấy username (thường là email)
			OAuth2User oauth2User = (OAuth2User) principal;
			username = (String) oauth2User.getAttribute("email"); // Hoặc sử dụng attribute khác nếu có
		}

		if (username == null) {
			throw new RuntimeException("User not authenticated");
		}
		User temp = userDao.findUserByEmail(username);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		BookCategory category = categoryDao.findById(id).get();
		category.setPersondelete(temp.getId());
		category.setDeleteday(timestamp.toString());
		categoryDao.save(category);
	}

	@Override
	public BookCategoryModel getOneBookCategoryById(Integer id) {
		BookCategory bookCategory = categoryDao.findById(id).get();
		BookCategoryModel bookCategoryModel = new BookCategoryModel();
		 bookCategoryModel.setName(bookCategory.getName());
		 bookCategoryModel.setNameSearch(bookCategory.getNamesearch());
		 bookCategoryModel.setLogo(bookCategory.getLogo());
		 bookCategoryModel.setBanner(bookCategory.getBanner());
		 bookCategoryModel.setDescribe(bookCategory.getDescription());
		return  bookCategoryModel;
	}

	@Override
	public BookCategoryModel updateBookCategory(BookCategoryModel bookCategoryModel) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = null;

		// Kiểm tra xem principal có phải là UserDetails hay không
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else if (principal instanceof OAuth2User) {
			// Nếu là OAuth2User (DefaultOAuth2User), lấy username (thường là email)
			OAuth2User oauth2User = (OAuth2User) principal;
			username = (String) oauth2User.getAttribute("email"); // Hoặc sử dụng attribute khác nếu có
		}

		if (username == null) {
			throw new RuntimeException("User not authenticated");
		}
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		User temp = userDao.findUserByEmail(username);
		
		BookCategory category = categoryDao.findById(bookCategoryModel.getId()).get();
		category.setName(bookCategoryModel.getName());
		category.setNamesearch(bookCategoryModel.getNameSearch());
		category.setLogo(bookCategoryModel.getLogo());
		category.setBanner(bookCategoryModel.getBanner());
		category.setDescription(bookCategoryModel.getDescribe());
		category.setUpdateday(timestamp.toString());
		category.setPersonupdate(temp.getId());
		categoryDao.save(category);
		return bookCategoryModel;
	}

	@Override
	public BookCategory getBookCategoryByNameSearch(String nameSearch) {
		return categoryDao.getBookCategoryByNameSearch(nameSearch);
	}
}
