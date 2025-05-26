package org.example.restaurantpos.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_order")
    private Order order;

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "amount_paid")
    private BigDecimal amountPaid;

    @Column(name = "is_paid")
    private boolean isPaid;
}
