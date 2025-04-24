package poly.store.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import poly.store.entity.Book;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowBook {
	private Book book;
	private int totalStar;
	private Long soldQuantity = 0L; // Thêm thuộc tính này
	// Constructor phù hợp với @Query
    public ShowBook(Book book, Long soldQuantity) {
        this.book = book;
        this.soldQuantity = soldQuantity != null ? soldQuantity : 0;
    }
}
