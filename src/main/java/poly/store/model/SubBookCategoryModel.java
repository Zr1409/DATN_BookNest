
package poly.store.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class thong tin truy van cua bang SubBookCategory
 * 
 * @author 
 * @version 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubBookCategoryModel {
	private int id;
	private String name;
	private int bookCategoryId;
	private String nameSearch;
}
