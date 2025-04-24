package poly.store.service;

import java.util.List;

import poly.store.entity.BookCategory;
import poly.store.model.BookCategoryModel;

public interface BookCategoryService {

	BookCategoryModel createBookCategory(BookCategoryModel bookCategoryModel);

	List<BookCategory> findAll();

	void delete(Integer id);

	BookCategoryModel getOneBookCategoryById(Integer id);

	BookCategoryModel updateBookCategory(BookCategoryModel bookCategoryModel);

	BookCategory getBookCategoryByNameSearch(String nameSearch);

}
