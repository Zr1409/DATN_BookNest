package poly.store.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.store.entity.WalletTransaction;
import poly.store.entity.Wallet;

import java.util.List;

@Repository
public interface WalletTransactionDao extends JpaRepository<WalletTransaction, Integer> {
	   // Tìm các giao dịch từ ví người gửi
    List<WalletTransaction> findByFromWallet(Wallet fromWallet);

    // Tìm các giao dịch đến ví người nhận
    List<WalletTransaction> findByToWallet(Wallet toWallet);
}
