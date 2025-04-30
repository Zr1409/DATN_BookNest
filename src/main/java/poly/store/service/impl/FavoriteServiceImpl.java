package poly.store.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import poly.store.dao.FavoriteDao;
import poly.store.dao.BookDao;
import poly.store.dao.UserDao;
import poly.store.entity.Favorite;
import poly.store.entity.Book;
import poly.store.entity.User;
import poly.store.model.BestSellerModel;
import poly.store.service.FavoriteService;

@Service
public class FavoriteServiceImpl implements FavoriteService {
	@Autowired
	BookDao bookDao;

	@Autowired
	FavoriteDao favoriteDao;

	@Autowired
	UserDao userDao;

	// @SuppressWarnings("null")
	@Override
	public Favorite create(int id) {
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

		Favorite favorite = new Favorite();

		if (temp != null) {

			favorite = favoriteDao.getOneFavorite(username, id);

			if (favorite == null) {
				favorite = new Favorite();
				Date date = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				String strDate = formatter.format(date);

				Book book = bookDao.findById(id).get();
				favorite.setBook(book);
				favorite.setUser(temp);
				favorite.setDate(strDate);
				favoriteDao.save(favorite);
			}

			else {
				favoriteDao.delete(favorite);
				favorite.setId(0);
			}

		}

		return favorite;
	}

	@Override
	public List<Favorite> getListFavoriteByEmail() {
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

		return favoriteDao.getListFavoriteByEmail(username);
	}

	@Override
	public void delete(int id) {
		Favorite favorite = favoriteDao.findById(id).get();
		favoriteDao.delete(favorite);
	}

	@Override
	public Favorite getOneFavorite(int id) {
		Favorite favorite = new Favorite();

		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = null;

		if (principal instanceof UserDetails) {
			// Đăng nhập thường (email + password)
			username = ((UserDetails) principal).getUsername();
		} else if (principal instanceof OAuth2User) {
			// Đăng nhập OAuth2 (Google, Facebook)
			username = ((OAuth2User) principal).getAttribute("email");
		}
		User temp = userDao.findUserByEmail(username);

		if (temp != null) {
			favorite = favoriteDao.getOneFavorite(username, id);
		} else {
			favorite = null;
		}
		return favorite;
	}

	@Override
	public List<BestSellerModel> getListBestSellerBook(Pageable topFour) {
		return favoriteDao.getListBestSellerBook(topFour);
	}

}
