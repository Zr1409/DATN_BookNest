package poly.store.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookReviewsModel {
	// @JsonProperty("productId") // Ánh xạ productId từ JSON thành bookId
	private int bookId;
	private String content;
	private int star;
}
