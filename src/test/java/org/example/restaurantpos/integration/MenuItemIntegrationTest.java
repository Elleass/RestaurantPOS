package org.example.restaurantpos.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.restaurantpos.entity.Category;
import org.example.restaurantpos.entity.MenuItem;
import org.example.restaurantpos.repository.CategoryRepository;
import org.example.restaurantpos.repository.MenuItemRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
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
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"spring.profiles.active=integration-test"})
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
public class MenuItemIntegrationTest {

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
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private MenuItemRepository menuItemRepository;

    private static Long itemId;
    private static Category testCategory;

    @BeforeAll
    static void setup(@Autowired CategoryRepository categoryRepo) {
        testCategory = categoryRepo.save(Category.builder()
                .categoryName("Starters-" + UUID.randomUUID())
                .build());
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void createMenuItem() throws Exception {
        MenuItem item = MenuItem.builder()
                .itemName("Bruschetta")
                .description("Toasted bread with tomato")
                .price(new BigDecimal("5.50"))
                .category(testCategory)
                .isAvailable(true)
                .build();

        String response = mockMvc.perform(post("/api/menu-items")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemName").value("Bruschetta"))
                .andReturn().getResponse().getContentAsString();

        itemId = objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void getMenuItemById() throws Exception {
        mockMvc.perform(get("/api/menu-items/" + itemId)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId));
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void updateMenuItem() throws Exception {
        MenuItem updated = MenuItem.builder()
                .itemName("Updated Bruschetta")
                .description("New desc")
                .price(new BigDecimal("6.00"))
                .category(testCategory)
                .isAvailable(false)
                .build();

        mockMvc.perform(put("/api/menu-items/" + itemId)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName").value("Updated Bruschetta"));
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void toggleAvailability() throws Exception {
        mockMvc.perform(patch("/api/menu-items/" + itemId + "/availability")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    void getAllMenuItems() throws Exception {
        mockMvc.perform(get("/api/menu-items")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    void deleteMenuItem() throws Exception {
        mockMvc.perform(delete("/api/menu-items/" + itemId)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        Assertions.assertFalse(menuItemRepository.findById(itemId).isPresent());
    }

    @Test
    void userWithoutAdminCannotCreateItem() throws Exception {
        MenuItem item = MenuItem.builder()
                .itemName("Blocked Item")
                .description("Blocked")
                .price(new BigDecimal("5.00"))
                .category(testCategory)
                .isAvailable(true)
                .build();

        mockMvc.perform(post("/api/menu-items")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isForbidden());
    }
}
