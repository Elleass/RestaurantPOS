package org.example.restaurantpos.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order_item") // Match DB column name
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_order", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "id_item", nullable = false)
    private MenuItem item;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "item_price", nullable = false)
    private BigDecimal itemPrice;
}
