package poly.store.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import poly.store.entity.InformationShop;

public interface InformationShopDao extends JpaRepository<InformationShop, Integer>{
	@Query("SELECT i FROM InformationShop i WHERE i.Deleteday IS NULL")
	List<InformationShop> getListInformationShop();

	@Query("SELECT i FROM InformationShop i WHERE i.Deleteday IS NULL AND i.active = true")
	InformationShop getOneInformationShop();


}
