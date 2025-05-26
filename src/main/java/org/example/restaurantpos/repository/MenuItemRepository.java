package org.example.restaurantpos.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.restaurantpos.entity.MenuItem;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {}