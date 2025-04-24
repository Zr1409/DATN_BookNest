
package poly.store.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class thong tin truy van cua bang Books
 * 
 * @author 
 * @version 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookModel {
	private int id;
	private String code;
	private String name;
	private String author;
	private int price;
	private int point;
	private int quantity;
	private String description;	
	private String image1;
	private String image2;
	private String image3;
	private String image4;
	private String image5;
	private boolean active;
	private int publicationYear;		
	private String 	weight;			
	private String 	dimensions;		
	private int pageCount;
	private  String format;
	private int publisherId;
	private int subBookCategoryId;
	private String nameSearch;
	private int sales;
}
