package org.example.restaurantpos.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.restaurantpos.entity.Order;
import org.example.restaurantpos.entity.Payment;
import org.example.restaurantpos.repository.OrderRepository;
import org.example.restaurantpos.repository.PaymentRepository;
import org.example.restaurantpos.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Payment createPayment(Payment payment) {
        Long orderId = payment.getOrder().getId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        payment.setOrder(order);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setPaid(true);

        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with ID: " + id));
    }

    @Override
    public Payment updatePayment(Long id, Payment updatedPayment) {
        Payment existing = getPaymentById(id);
        existing.setPaymentMethod(updatedPayment.getPaymentMethod());
        existing.setAmountPaid(updatedPayment.getAmountPaid());
        existing.setPaid(updatedPayment.isPaid());
        return paymentRepository.save(existing);
    }

    @Override
    public boolean isOrderPaid(Long orderId) {
        return paymentRepository.existsByOrderIdAndIsPaidTrue(orderId);
    }
}
