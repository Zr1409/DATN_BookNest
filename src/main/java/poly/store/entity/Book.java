package poly.store.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Books")
public class Book implements Serializable{
	// Thong tin id san pham
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	// Thong tin ma san pham
	private String code;

	// Thong tin ten san pham
	private String name;

	// Thong tin tac gia
	private String author;
	
	// Thong tin gia san pham
	private int price;

	// Thong tin so luong san pham
	private int quantity;

	// Thong tin so luot xem
	private int views;

	// Mo ta san pham
	private String description;


	// Thong tin hinh anh 1
	private String image1;

	// Thong tin hinh anh 2
	private String image2;

	// Thong tin hinh anh 3
	private String image3;

	// Thong tin hinh anh 4
	private String image4;

	// Thong tin hinh anh 5
	private String image5;

	// Hien thi san pham hay khong
	private boolean active;
	
	// Thong tin gia khuyen mai
	private int sales;
		
	// Hien thi nam xb
	private int publicationYear;
	
	// Hien thi trong luong
	private String 	weight;
		
	// Hien thi kich thuoc
	private String 	dimensions;
		
	// Hien thi so trang
	private int pageCount;
		
	// Hien thi hinh thuc
	private  String format;
	
	// Hien thi ten dung de tim kiem
	private String Namesearch;		
		
	// Thong tin ngay tao	
	private String Createday;

	// Thong tin ma nguoi tao
	private Integer Personcreate;

	// Thong tin ngay xoa
	private String Deleteday;

	// Thong tin nguoi xoa
	private Integer Persondelete;

	// Thong tin ngay cap nhat
	private String Updateday;

	// Thong tin ma nguoi cap nhat
	private Integer Personupdate;

	// Thong tin nha san xuat
	@ManyToOne
	@JoinColumn(name = "Publisher_Id")
	Publisher publisher;

	// Thong tin danh muc con
	@ManyToOne
	@JoinColumn(name = "SubBookCategory_Id")
	SubBookCategory subBookCategory;
	
	@JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "book")
	List<Favorite> listFavorite;
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "book")
	List<BookReviews> listBookReviews;
}
