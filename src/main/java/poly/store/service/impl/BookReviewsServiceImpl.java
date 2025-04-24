package poly.store.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import poly.store.dao.BookReviewsDao;
import poly.store.dao.BookDao;
import poly.store.dao.UserDao;
import poly.store.entity.BookReviews;
import poly.store.entity.Book;
import poly.store.entity.User;
import poly.store.model.BookReviewsModel;
import poly.store.service.BookReviewsService;

@Service
public class BookReviewsServiceImpl implements BookReviewsService {
	@Autowired
	BookReviewsDao bookReviewsDao;

	@Autowired
	UserDao userDao;

	@Autowired
	BookDao bookDao;

	@Override
	public List<BookReviews> getListBookReviewsByBookId(Integer id) {
		return bookReviewsDao.getListBookReviewsByBookId(id);
	}

	@Override
	public BookReviewsModel createBookReviews(BookReviewsModel bookReviewsModel) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();

		User temp = userDao.findUserByEmail(username);

		Book book = bookDao.findById(bookReviewsModel.getBookId()).get();

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String strDate = formatter.format(date);

		BookReviews bookReviews = new BookReviews();

		bookReviews.setContent(bookReviewsModel.getContent());
		bookReviews.setStar(bookReviewsModel.getStar());
		bookReviews.setDate(strDate);
		bookReviews.setBook(book);
		bookReviews.setUser(temp);
		bookReviews.setStatus("0");
		bookReviewsDao.save(bookReviews);

		return bookReviewsModel;
	}

	@Override
	public List<BookReviews> getListBookReviewsPending() {
		return bookReviewsDao.getListBookReviewsPending();
	}

	@Override
	public BookReviews getBookReviewsByBookReviewsId(Integer id) {
		return bookReviewsDao.getBookReviewsByBookReviewsId(id);
	}

	@Override
	public void approveBookReviews(Integer id) {
		BookReviews BookReviews = bookReviewsDao.findById(id).get();
		BookReviews.setStatus("1");
		bookReviewsDao.save(BookReviews);
	}

	@Override
	public void delete(Integer id) {
		BookReviews BookReviews = bookReviewsDao.findById(id).get();
		bookReviewsDao.delete(BookReviews);
	}

	@Override
	public List<BookReviews> getListBookReviewsChecked() {
		return bookReviewsDao.getListBookReviewsChecked();
	}

	@Override
	public int getCountBookReviewsByBookNameSearch(String nameSearch) {
		return bookReviewsDao.getCountBookReviewsByBookNameSearch(nameSearch);
	}

	@Override
	public int getAllStarBookReviewsByBookNameSearch(String nameSearch) {
		int result = 0;
		int totalStar = 0;
		List<Integer> listStar = bookReviewsDao.getAllStarBookReviewsByBookNameSearch(nameSearch);
		if(listStar.isEmpty() == false) {
			for(int i = 0; i < listStar.size(); i++) {
				totalStar = totalStar + listStar.get(i);
				System.out.println(totalStar);
			}
			result = Math.round(totalStar / (listStar.size()));
		}
		return result;
	}
	
	@Override
	public int countPendingBookReviews() {
	    return bookReviewsDao.countPendingBookReviewss();
	}


	@Override
	public int countSuccessBookReviews() {
	    return bookReviewsDao.countSuccessBookReviewss();
	}

}
