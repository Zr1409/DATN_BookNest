
package poly.store.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class thong tin truy van cua bang BookCategory
 * 
 * @author 
 * @version 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCategoryModel {
	private int id;
	private String name;
	private String banner;
	private String logo;
	private String describe;
	private String nameSearch;
}
