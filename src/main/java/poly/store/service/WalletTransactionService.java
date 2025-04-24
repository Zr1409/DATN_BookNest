package poly.store.service;

import java.util.List;
import poly.store.entity.Wallet;
import poly.store.entity.WalletTransaction;

public interface WalletTransactionService {
    void save(WalletTransaction transaction);
    // Tìm các giao dịch từ ví người gửi
    List<WalletTransaction> findByFromWallet(Wallet fromWallet);

    // Tìm các giao dịch đến ví người nhận
    List<WalletTransaction> findByToWallet(Wallet toWallet);
}
