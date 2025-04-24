package poly.store.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import poly.store.entity.Wallet;
import poly.store.entity.User;

@Repository
public interface WalletDao extends JpaRepository<Wallet, Integer> {
    Wallet findByUser(User user);
}
