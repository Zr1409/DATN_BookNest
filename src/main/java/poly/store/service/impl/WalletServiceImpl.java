package poly.store.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import poly.store.dao.WalletDao;
import poly.store.entity.User;
import poly.store.entity.Wallet;
import poly.store.service.WalletService;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    WalletDao walletDao;

    @Override
    public Wallet findByUser(User user) {
        return walletDao.findByUser(user);
    }

    @Override
    public void save(Wallet wallet) {
        walletDao.save(wallet);
    }

    @Override
    public void updateBalance(User user, BigDecimal amount) {
        Wallet wallet = walletDao.findByUser(user);
        if (wallet != null) {
            wallet.setBalance(wallet.getBalance().add(amount));
            walletDao.save(wallet);
        }
    }
}
