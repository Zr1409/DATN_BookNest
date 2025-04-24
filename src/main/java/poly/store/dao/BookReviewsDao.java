package poly.store.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import poly.store.entity.BookReviews;

public interface BookReviewsDao extends JpaRepository<BookReviews, Integer>{
	@Query("SELECT c FROM BookReviews c WHERE c.status = 1 AND c.book.id = :uid")
	List<BookReviews> getListBookReviewsByBookId(@Param("uid") Integer id);
	
	@Query("SELECT c FROM BookReviews c WHERE c.status = 0")
	List<BookReviews> getListBookReviewsPending();
	
	@Query("SELECT c FROM BookReviews c WHERE c.status = 1")
	List<BookReviews> getListBookReviewsChecked();
	
	@Query("SELECT c FROM BookReviews c WHERE c.id = :uid")
	BookReviews getBookReviewsByBookReviewsId(@Param("uid") Integer id);
	
	@Query("SELECT COUNT(c) FROM BookReviews c WHERE c.book.Namesearch = :unameSearch AND c.status = 1")
	int getCountBookReviewsByBookNameSearch(@Param("unameSearch") String nameSearch);
	
	@Query("SELECT c.star FROM BookReviews c WHERE c.book.Namesearch = :unameSearch AND c.status = 1")
	List<Integer> getAllStarBookReviewsByBookNameSearch(@Param("unameSearch") String nameSearch);
	
	@Query("SELECT COUNT(c) FROM BookReviews c WHERE c.status = 0")
	int countPendingBookReviewss();
	
	@Query("SELECT COUNT(c) FROM BookReviews c WHERE c.status = 1")
	int countSuccessBookReviewss();
}
