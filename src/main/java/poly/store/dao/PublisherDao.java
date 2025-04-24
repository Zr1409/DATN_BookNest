package poly.store.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import poly.store.entity.Publisher;

public interface PublisherDao extends JpaRepository<Publisher, Integer>{
	@Query("SELECT m FROM Publisher m WHERE m.Deleteday = null")
	List<Publisher> getListPublisher();
}
