package poly.store.service;

import java.math.BigDecimal;
import poly.store.entity.User;
import poly.store.entity.Wallet;

public interface WalletService {
    Wallet findByUser(User user);
    void save(Wallet wallet);
    void updateBalance(User user, BigDecimal amount); // cộng/trừ tiền
}
