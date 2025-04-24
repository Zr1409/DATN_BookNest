package poly.store.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import poly.store.entity.Favorite;
import poly.store.model.BestSellerModel;

public interface FavoriteDao extends JpaRepository<Favorite, Integer>{
	@Query("SELECT f FROM Favorite f WHERE f.user.email LIKE ?1 and f.book.Deleteday = null")
	List<Favorite> getListFavoriteByEmail(String email);
	
	@Query("SELECT f FROM Favorite f WHERE f.user.email LIKE ?1 and f.book.id = ?2")
	Favorite getOneFavorite(String email, int id);
	
	@Query("SELECT new BestSellerModel(f.book, count(f.book)) FROM Favorite f WHERE f.book.Deleteday = null AND f.book.active = 1 GROUP BY f.book ORDER BY count(f.book) DESC")
	List<BestSellerModel> getListBestSellerBook(Pageable pageable);
}
