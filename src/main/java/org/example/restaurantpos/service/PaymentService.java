package org.example.restaurantpos.service;

import org.example.restaurantpos.entity.Payment;

import java.util.List;

public interface PaymentService {
    Payment createPayment(Payment payment);
    List<Payment> getAllPayments();
    Payment getPaymentById(Long id);
    Payment updatePayment(Long id, Payment payment);
    boolean isOrderPaid(Long orderId);
}
