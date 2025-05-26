package org.example.restaurantpos.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.restaurantpos.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {}