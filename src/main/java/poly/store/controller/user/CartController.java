package poly.store.controller.user;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import poly.store.common.Constants;
import poly.store.entity.Discount;
import poly.store.entity.User;
import poly.store.model.AlertModel;
import poly.store.model.BestSellerModel;
import poly.store.model.CartModel;
import poly.store.model.BookModel;
import poly.store.model.ShowBook;
import poly.store.service.BookReviewsService;
import poly.store.service.DiscountService;
import poly.store.service.OrderService;
import poly.store.service.ParamService;
import poly.store.service.BookService;
import poly.store.service.SessionService;
import poly.store.service.impl.DiscountServiceImpl;
import poly.store.service.impl.OrderServiceImpl;
import poly.store.service.impl.ShoppingCartServiceImpl;
import poly.store.service.impl.UserServiceImpl;

@Controller
public class CartController {
	@Autowired
	ShoppingCartServiceImpl shoppingCartServiceImpl;
	
	@Autowired
	DiscountServiceImpl discountServiceImpl;
	
	@Autowired
	DiscountService discountService;
	
	@Autowired
	SessionService sessionService;
	
	@Autowired
	ParamService paramService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	OrderServiceImpl orderServiceImpl;
	
	@Autowired
	BookReviewsService bookReviewsService;
	
	@Autowired
	BookService productService;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@GetMapping("/shop/cart")
	public String index(Model model, @ModelAttribute("alertModel") AlertModel alertModel) {
	    model.addAttribute("showDiscount", false);
	    shoppingCartServiceImpl.clearDiscount();
	    model.addAttribute("cart", shoppingCartServiceImpl);

	    if (alertModel == null) {
	        alertModel = new AlertModel();
	    }
	    model.addAttribute("alertModel", alertModel);

	    return Constants.USER_DISPLAY_SHOPPING_CART;
	}

	
	@PostMapping("/cart/update/{id}")
	public String update(@PathVariable("id") Integer id, HttpServletRequest req, RedirectAttributes redirectAttrs) {
	    String qty = req.getParameter("quantity");
	    BookModel product = productService.getOneBookById(id);
	    int requestedQty = Integer.parseInt(qty);

	    AlertModel alertModel = new AlertModel();

	    if (requestedQty > product.getQuantity()) {
	        alertModel.setAlert("alert-danger");
	        alertModel.setContent("Số lượng sản phẩm vượt quá số lượng tồn kho!");
	        alertModel.setDisplay(true);
	    } else {
	    	shoppingCartServiceImpl.update(id, requestedQty);
	        alertModel.setAlert("alert-success");
	        alertModel.setContent("Cập nhật số lượng thành công!");
	        alertModel.setDisplay(true);
	    }

	    redirectAttrs.addFlashAttribute("alertModel", alertModel);
	    redirectAttrs.addFlashAttribute("cart", shoppingCartServiceImpl);  // Giữ lại giỏ hàng
	    return "redirect:/shop/cart";
	}




	
	@RequestMapping("/cart/remove/{id}")
	public String remove(@PathVariable("id") Integer id) {
		shoppingCartServiceImpl.remove(id);	
		sessionService.set("sessionProduct", shoppingCartServiceImpl);
		return "redirect:/shop/cart";
	}
	
	@GetMapping("/shop/cart/discount")
	public String getDiscount() {
		return "redirect:/shop/cart";
	}
	
	@PostMapping("/shop/cart/discount")
	public String discount(Model model) {
		String code = paramService.getString("discount", "");
		
		 // Kiểm tra xem mã giảm giá đã được sử dụng bởi tài khoản này chưa
	    User currentUser = userServiceImpl.getCurrentUser(); // Lấy thông tin người dùng hiện tại
	    if (currentUser == null) {
	        return "redirect:/login";
	    }
	    if (discountServiceImpl.isDiscountUsedByUser(currentUser.getId(), code)) {
	        AlertModel alertModel = new AlertModel();
	        alertModel.setAlert("alert-warning");
	        alertModel.setContent("Bạn đã sử dụng mã giảm giá này!");
	        alertModel.setDisplay(true);
	        model.addAttribute("alertModel", alertModel);
	        model.addAttribute("cart", shoppingCartServiceImpl);
	        model.addAttribute("showDiscount", true);
	        return Constants.USER_DISPLAY_SHOPPING_CART;
	    }
		Discount discount = discountService.getDiscountByCode(code);
		
		AlertModel alertModel = new AlertModel();
		
		if(discount == null) {
			shoppingCartServiceImpl.clearDiscount();
			shoppingCartServiceImpl.getAmount();
			alertModel.setAlert("alert-warning");
			alertModel.setContent("Mã giảm giá không tồn tại!");
			alertModel.setDisplay(true);
		}
		
		else {
			if(shoppingCartServiceImpl.getAmount() >= discount.getMoneylimit()) {
				shoppingCartServiceImpl.addDiscount(discount.getId(), discount);
				shoppingCartServiceImpl.getAmount();
				alertModel.setAlert("alert-success");
				alertModel.setContent("Bạn đã áp dụng mã giảm giá thành công!");
				alertModel.setDisplay(true);
			}
			else {
				shoppingCartServiceImpl.clearDiscount();
				shoppingCartServiceImpl.getAmount();
				alertModel.setAlert("alert-warning");
				alertModel.setContent("Mã giảm giá không tồn tại!");
				alertModel.setDisplay(true);			
			}
		}
		
		model.addAttribute("showDiscount", true);
		model.addAttribute("discount", code);
		model.addAttribute("alertModel", alertModel);	
		model.addAttribute("cart", shoppingCartServiceImpl);
		return Constants.USER_DISPLAY_SHOPPING_CART;
	}
	
	@ModelAttribute("total")
	public int tolal() {
		List<CartModel> list = new ArrayList<>(shoppingCartServiceImpl.getItems());
		int total = 0;
		for(CartModel i: list) {
			// Kiểm tra giá trị sale
	        int priceToUse;
			if (i.getBook().getSales() != 0)
				priceToUse = i.getBook().getSales();
			else
				priceToUse = i.getBook().getPrice();
			total = total + priceToUse * i.getQuantity();
		}
		return total;
	}
	
	@ModelAttribute("listBestSeller")
	public List<ShowBook> getListBestSeller(Model model){
		Pageable topFour = PageRequest.of(0, 4);
		
		List<BestSellerModel> list = orderService.getListBestSellerBook(topFour);
		
		List<ShowBook> listProduct = new ArrayList<ShowBook>();
		
		for(BestSellerModel bestSeller: list) {
			ShowBook showProduct = new ShowBook();
			int totalStar = bookReviewsService.getAllStarBookReviewsByBookNameSearch(bestSeller.getBook().getNamesearch());
			showProduct.setBook(bestSeller.getBook());
			showProduct.setTotalStar(totalStar);
			listProduct.add(showProduct);
		}		
		return listProduct;
	}
}
