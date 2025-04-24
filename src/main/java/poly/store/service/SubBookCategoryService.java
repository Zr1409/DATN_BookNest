package poly.store.service;

import java.util.List;

import poly.store.entity.SubBookCategory;
import poly.store.model.SubBookCategoryModel;

public interface SubBookCategoryService {

	SubBookCategoryModel createSubBookCategory(SubBookCategoryModel subBookCategoryModel);

	List<SubBookCategory> findAll();

	void delete(Integer id);

	SubBookCategoryModel getSubBookCategoryById(Integer id);

	SubBookCategoryModel updateSubBookCategory(SubBookCategoryModel subBookCategoryModel);

}
