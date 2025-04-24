package poly.store.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import poly.store.dao.WalletTransactionDao;
import poly.store.entity.Wallet;
import poly.store.entity.WalletTransaction;
import poly.store.service.WalletTransactionService;

@Service
public class WalletTransactionServiceImpl implements WalletTransactionService {

    @Autowired
    WalletTransactionDao walletTransactionDao;

    @Override
    public void save(WalletTransaction transaction) {
        walletTransactionDao.save(transaction);
    }

    @Override
    public List<WalletTransaction> findByFromWallet(Wallet fromWallet) {
        return walletTransactionDao.findByFromWallet(fromWallet);
    }

    @Override
    public List<WalletTransaction> findByToWallet(Wallet toWallet) {
        return walletTransactionDao.findByToWallet(toWallet);
    }
}
