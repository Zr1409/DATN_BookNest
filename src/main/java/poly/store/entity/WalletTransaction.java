package poly.store.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "WalletTransactions")
public class WalletTransaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// Ví người gửi
	@ManyToOne
	@JoinColumn(name = "FromWalletId")
	private Wallet fromWallet;

	// Ví người nhận
	@ManyToOne
	@JoinColumn(name = "ToWalletId")
	private Wallet toWallet;

	@Column(nullable = false)
	private BigDecimal amount;

	@Column(length = 50, nullable = false)
	private String type;

	private String createDate;

}
