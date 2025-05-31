package org.example.restaurantpos.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.restaurantpos.entity.*;
import org.example.restaurantpos.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
public class OrderIntegrationTest extends BaseIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private RoleRepository roleRepository;

    private Long orderId;

    private Customer customer;
    private User user;

    @BeforeEach
    void setup() {
        Role role = roleRepository.findByRoleName("ROLE_ADMIN").orElseGet(() ->
                roleRepository.save(Role.builder()
                        .roleName("ROLE_ADMIN")
                        .description("Admin role")
                        .build()));


        user = userRepository.findByUsername("orderuser")
                .orElseGet(() -> userRepository.save(User.builder()
                        .username("orderuser")
                        .password("secure")
                        .isLocked(false)
                        .role(role)
                        .build()));

        Order order = Order.builder()
                .customer(customer)
                .user(user)
                .orderTime(LocalDateTime.now())
                .status("PENDING")
                .totalAmount(new BigDecimal("30.00"))
                .build();

        Order savedOrder = orderRepository.save(order);
        this.orderId = savedOrder.getId();
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void createOrder() throws Exception {
        Order newOrder = Order.builder()
                .customer(customer)
                .user(user)
                .orderTime(LocalDateTime.now())
                .status("NEW")
                .totalAmount(new BigDecimal("40.00"))
                .build();

        mockMvc.perform(post("/api/orders")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOrder)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalAmount").value(40.00));
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void getOrderById() throws Exception {
        mockMvc.perform(get("/api/orders/" + orderId)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId));
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void updateOrder() throws Exception {
        Order updated = Order.builder()
                .customer(customer)
                .user(user)
                .orderTime(LocalDateTime.now())
                .status("COMPLETED")
                .totalAmount(new BigDecimal("30.00"))
                .build();

        mockMvc.perform(put("/api/orders/" + orderId)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void getAllOrders() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    void deleteOrder() throws Exception {
        mockMvc.perform(delete("/api/orders/" + orderId)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        Assertions.assertFalse(orderRepository.findById(orderId).isPresent());
    }

    @Test
    void userCannotAccessAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("basicuser").roles("USER")))
                .andExpect(status().isForbidden());
    }
}
