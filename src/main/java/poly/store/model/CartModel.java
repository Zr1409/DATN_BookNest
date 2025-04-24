package poly.store.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import poly.store.entity.Book;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartModel {
	private int id;
	private Book book;
//	private String name;
//	private String image;
	private int quantity;
//	private int discount = 0;
//	private int price;
}
