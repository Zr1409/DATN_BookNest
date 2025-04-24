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
@Table(name = "Orders")
public class Order implements Serializable {
	// Thong tin order id
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String code;

	// Thong tin dia chi nguoi dung
	@ManyToOne
	@JoinColumn(name = "Address_Id")
	Address address;

	@JsonIgnore
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;

	// Thong tin ma giam gia
	@ManyToOne
	@JoinColumn(name = "Discount_Id")
	Discount discount;


	// Thong tin ngay mua
	private String date;

	// Thong tin phuong thuc van chuyen
	private String method;

	// Thong tin trang thai van chuyen
	private String status;

	// Thong tin ghi chu
	private String comment;

	// Phi ship
	private Integer shippingFee;
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Payment> payment;

}
