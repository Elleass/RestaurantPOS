package org.example.restaurantpos.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.restaurantpos.entity.Order;
import org.example.restaurantpos.entity.Payment;
import org.example.restaurantpos.repository.OrderRepository;
import org.example.restaurantpos.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;
    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        order = new Order();
        order.setId(1L);

        payment = new Payment();
        payment.setId(1L);
        payment.setOrder(order);
        payment.setPaymentMethod("CARD");
        payment.setAmountPaid(BigDecimal.valueOf(50.0));
        payment.setPaid(false);
    }

    @Test
    void shouldCreatePayment() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.createPayment(payment);

        assertTrue(result.isPaid());
        assertEquals(order, result.getOrder());
        assertEquals("CARD", result.getPaymentMethod());
        assertNotNull(result.getPaymentTime());

        verify(orderRepository).findById(1L);
        verify(paymentRepository).save(payment);
    }

    @Test
    void shouldThrowWhenCreatingPaymentForInvalidOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> paymentService.createPayment(payment));
    }

    @Test
    void shouldGetAllPayments() {
        when(paymentRepository.findAll()).thenReturn(List.of(payment));
        List<Payment> result = paymentService.getAllPayments();
        assertEquals(1, result.size());
        assertEquals(payment.getPaymentMethod(), result.get(0).getPaymentMethod());
    }

    @Test
    void shouldGetPaymentById() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        Payment result = paymentService.getPaymentById(1L);
        assertEquals("CARD", result.getPaymentMethod());
    }

    @Test
    void shouldThrowWhenPaymentNotFoundById() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> paymentService.getPaymentById(1L));
    }

    @Test
    void shouldUpdatePayment() {
        Payment updated = new Payment();
        updated.setPaymentMethod("CASH");
        updated.setAmountPaid(BigDecimal.valueOf(100.0));
        updated.setPaid(true);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.updatePayment(1L, updated);

        assertEquals("CASH", result.getPaymentMethod());
        assertEquals(BigDecimal.valueOf(100.0), result.getAmountPaid());
        assertTrue(result.isPaid());
    }

    @Test
    void shouldCheckIfOrderIsPaid() {
        when(paymentRepository.existsByOrderIdAndIsPaidTrue(1L)).thenReturn(true);
        assertTrue(paymentService.isOrderPaid(1L));
    }
}
