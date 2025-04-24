package poly.store.service;

import java.util.List;

import poly.store.entity.BookReviews;
import poly.store.model.BookReviewsModel;

public interface BookReviewsService {

	List<BookReviews> getListBookReviewsByBookId(Integer id);

	BookReviewsModel createBookReviews(BookReviewsModel commentModel);

	List<BookReviews> getListBookReviewsPending();

	BookReviews getBookReviewsByBookReviewsId(Integer id);

	void approveBookReviews(Integer id);

	void delete(Integer id);

	List<BookReviews> getListBookReviewsChecked();

	int getCountBookReviewsByBookNameSearch(String nameSearch);

	int getAllStarBookReviewsByBookNameSearch(String nameSearch);
	
	// Thêm phương thức đếm số lượng đánh giá theo trạng thái
	int countPendingBookReviews();

	int countSuccessBookReviews();



}
