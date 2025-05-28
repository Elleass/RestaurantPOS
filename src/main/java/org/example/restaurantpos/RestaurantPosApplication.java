package org.example.restaurantpos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("org.example.restaurantpos.entity")
@EnableJpaRepositories("org.example.restaurantpos.repository")
public class RestaurantPosApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestaurantPosApplication.class, args);
    }
}
