package poly.store.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import poly.store.entity.Book;
import poly.store.entity.Discount;
import poly.store.model.CartModel;
import poly.store.service.ShoppingCartService;

@SessionScope
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ShoppingCartServiceImpl implements ShoppingCartService {

	private  Map<Integer, CartModel> map = new HashMap<>();

	private  Map<Integer, Discount> mapDiscount = new HashMap<>();

	@Override
	public void add(Integer id, CartModel entity) {
		if (map.get(id) != null) {
			this.update(id, entity.getQuantity() + 1);
		} else {
			map.put(id, entity);
		}

	}

	@Override
	public void addDiscount(Integer id, Discount entity) {
		mapDiscount.put(id, entity);
	}

	@Override
	public double getAmount() {
		double amount = 0;
		Set<Integer> set = map.keySet();
		for (Integer i : set) {
			// Lấy giá trị sách
			Book book = map.get(i).getBook();

			// Kiểm tra nếu sách có giá trị sale
			double price = (book.getSales() != 0) ? book.getSales() : book.getPrice();

			// Tính tổng tiền sử dụng giá sale (nếu có) hoặc giá gốc
			amount += map.get(i).getQuantity() * price;
		}

		if (this.getDiscount() != null) {
			try {
				amount = amount - this.getDiscount().getPrice();
			} catch (Exception e) {

			}
			System.out.println(amount);
		}

		return amount;
	}

	@Override
	public void remove(Integer id) {
		map.remove(id);
	}

	@Override
	public void update(Integer id, int qty) {
		map.get(id).setQuantity(qty);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Collection<CartModel> getItems() {
		return map.values();
	}

//	@Override
//	public int getCount() {
//		int count = 0;
//		Set<Integer> set = map.keySet();
//		for (Integer i : set) {
//			count++;
//		}
//		return count;
//	}
	@Override
	public int getCount() {
		if (map == null || map.isEmpty()) {
			return 0; // Tránh lỗi NullPointerException
		}
		return map.size(); // Trả về số lượng phần tử trong map (tương đương keySet().size())
	}

	@Override
	public int getCountAllProduct() {
		int count = 0;
		Set<Integer> set = map.keySet();
		for (Integer i : set) {
			count += map.get(i).getQuantity();
		}
		return count;
	}

	@Override
	public Discount getDiscount() {
		Discount discount = new Discount();
		Set<Integer> set = mapDiscount.keySet();
		for (Integer i : set) {
			discount = mapDiscount.get(i);
		}
		return discount;
	}

	@Override
	public void clearDiscount() {
		mapDiscount.clear();
	}

}
