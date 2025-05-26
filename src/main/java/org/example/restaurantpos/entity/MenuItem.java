package org.example.restaurantpos.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
@Entity
@Table(name = "menu_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name")
    private String itemName;

    private String description;
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "id_category")
    private Category category;

    @Column(name = "is_available")
    private boolean isAvailable;
}