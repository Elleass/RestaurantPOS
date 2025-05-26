package org.example.restaurantpos.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.restaurantpos.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {}