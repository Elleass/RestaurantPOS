package org.example.restaurantpos.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.restaurantpos.entity.*;
import org.example.restaurantpos.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"spring.profiles.active=integration-test"})
@AutoConfigureMockMvc
@Testcontainers
public class OrderItemIntegrationTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private MenuItemRepository menuItemRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;

    private MenuItem menuItem;
    private Order order;

    @BeforeEach
    void setup() {
        Role role = roleRepository.save(Role.builder()
                .roleName("ROLE_ADMIN")
                .description("Admin role")
                .build());

        User user = userRepository.save(User.builder()
                .username("test-admin-" + UUID.randomUUID())
                .password("password")
                .isLocked(false)
                .role(role)
                .build());

        Customer customer = customerRepository.save(Customer.builder()
                .name("Customer A")
                .phoneNumber("1234567890")
                .email("cust-" + UUID.randomUUID() + "@test.com")
                .build());

        Category category = categoryRepository.save(Category.builder()
                .categoryName("Main-" + UUID.randomUUID())
                .build());

        menuItem = menuItemRepository.save(MenuItem.builder()
                .itemName("Burger")
                .description("Juicy")
                .price(BigDecimal.valueOf(9.99))
                .isAvailable(true)
                .category(category)
                .build());

        order = orderRepository.save(Order.builder()
                .user(user)
                .customer(customer)
                .orderTime(LocalDateTime.now())
                .status("PENDING")
                .totalAmount(BigDecimal.valueOf(0))
                .build());
    }
    @Disabled
    @Test
    void shouldAddItemToOrder() throws Exception {
        OrderItem orderItem = OrderItem.builder()
                .item(MenuItem.builder().id(menuItem.getId()).build())
                .quantity(2)
                .itemPrice(menuItem.getPrice())
                .build();

        String response = mockMvc.perform(post("/api/orders/" + order.getId() + "/items")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderItem)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(2))
                .andReturn().getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        assertThat(json.get("quantity").asInt()).isEqualTo(2);
    }
    @Disabled
    @Test
    void shouldUpdateOrderItemQuantity() throws Exception {
        OrderItem existingItem = orderItemRepository.save(OrderItem.builder()
                .order(order)
                .item(menuItem)
                .quantity(1)
                .itemPrice(menuItem.getPrice())
                .build());

        OrderItem updated = OrderItem.builder()
                .id(existingItem.getId())
                .order(Order.builder().id(order.getId()).build())
                .item(MenuItem.builder().id(menuItem.getId()).build())
                .quantity(4)
                .itemPrice(menuItem.getPrice())
                .build();

        String response = mockMvc.perform(put("/api/order-items/" + existingItem.getId())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(4))
                .andReturn().getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        assertThat(json.get("quantity").asInt()).isEqualTo(4);
    }
}
