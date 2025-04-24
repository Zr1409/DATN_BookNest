
package poly.store.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class chua thong tin Category
 * 
 * @author 
 * @version 
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BookCategories", schema = "dbo")


public class BookCategory implements Serializable {
	// Thong tin category id
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	// Thong tin ten category
	private String name;

	// Thong tin hinh dai dien
	private String logo;

	// Thong tin banner
	private String banner;

	// Thong tin mo ta
	private String description;

	// Thong tin ngay tao
	private String Createday;

	// Thong tin ma nguoi tao
	private int Personcreate;

	// Thong tin ngay xoa
	private String Deleteday;

	// Thong tin nguoi xoa
	private int Persondelete;

	// Thong tin ngay cap nhat
	private String Updateday;

	// Thong tin ma nguoi cap nhat
	private int Personupdate;

	// Thong tin ten danh muc dung de search
	private String Namesearch;

	@JsonIgnore
	@OneToMany(mappedBy = "bookCategory")
	List<SubBookCategory> listSubBookCategory;
	
	// Danh sach blogs
	@JsonIgnore
	@OneToMany(mappedBy = "bookCategory")
	List<Blog> listBlog;
}
