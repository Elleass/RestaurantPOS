package org.example.restaurantpos.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.restaurantpos.entity.Customer;
import org.example.restaurantpos.entity.Payment;
import org.example.restaurantpos.entity.Role;
import org.example.restaurantpos.entity.User;
import org.example.restaurantpos.repository.CustomerRepository;
import org.example.restaurantpos.repository.OrderRepository;
import org.example.restaurantpos.repository.PaymentRepository;
import org.example.restaurantpos.repository.RoleRepository;
import org.example.restaurantpos.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"spring.profiles.active=integration-test"})
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PaymentIntegrationTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void registerPgProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private RoleRepository roleRepository;

    private Long paymentId;
    private org.example.restaurantpos.entity.Order testOrder;

    @BeforeEach
    void setupEntities() throws Exception {
        if (testOrder == null) {
            Role role = roleRepository.save(Role.builder()
                    .roleName("ROLE_ADMIN")
                    .description("Admin role")
                    .build());

            Customer customer = customerRepository.save(Customer.builder()
                    .name("Test Customer")
                    .phoneNumber("123456789")
                    .email("cust-" + UUID.randomUUID() + "@test.com")
                    .build());

            User user = userRepository.save(User.builder()
                    .username("testuser-" + UUID.randomUUID())
                    .password("pass")
                    .isLocked(false)
                    .role(role)
                    .build());

            testOrder = orderRepository.save(org.example.restaurantpos.entity.Order.builder()
                    .customer(customer)
                    .user(user)
                    .orderTime(LocalDateTime.now())
                    .status("PENDING")
                    .totalAmount(new BigDecimal("20.00"))
                    .build());

            Payment payment = Payment.builder()
                    .order(testOrder)
                    .paymentTime(LocalDateTime.now())
                    .paymentMethod("CARD")
                    .amountPaid(new BigDecimal("20.00"))
                    .isPaid(true)
                    .build();

            paymentId = paymentRepository.save(payment).getId();
        }
    }

    @Test
    @Order(1)
    void createPayment() throws Exception {
        Payment payment = Payment.builder()
                .order(testOrder)
                .paymentTime(LocalDateTime.now())
                .paymentMethod("CARD")
                .amountPaid(new BigDecimal("20.00"))
                .isPaid(true)
                .build();

        mockMvc.perform(post("/api/payments")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amountPaid").value(20.00));
    }

    @Test
    @Order(2)
    void getPaymentById() throws Exception {
        mockMvc.perform(get("/api/payments/" + paymentId)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paymentId));
    }

    @Test
    @Order(3)
    void updatePayment() throws Exception {
        Payment updated = Payment.builder()
                .order(testOrder)
                .paymentTime(LocalDateTime.now())
                .paymentMethod("CASH")
                .amountPaid(new BigDecimal("25.00"))
                .isPaid(true)
                .build();

        mockMvc.perform(put("/api/payments/" + paymentId)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentMethod").value("CASH"));
    }

    @Test
    @Order(4)
    void getAllPayments() throws Exception {
        mockMvc.perform(get("/api/payments")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(5)
    void getPaymentStatusForOrder() throws Exception {
        mockMvc.perform(get("/api/payments/order/" + testOrder.getId() + "/payment-status")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void userCannotAccessAdminOnlyEndpoints() throws Exception {
        mockMvc.perform(get("/api/payments")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }
}
