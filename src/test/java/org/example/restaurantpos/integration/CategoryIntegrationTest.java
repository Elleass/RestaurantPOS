package org.example.restaurantpos.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.restaurantpos.entity.Category;
import org.example.restaurantpos.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@Testcontainers
public class CategoryIntegrationTest extends BaseIntegrationTest {


    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CategoryRepository categoryRepository;

    @BeforeEach
    void setup() {
        categoryRepository.save(Category.builder()
                .categoryName("Appetizers-" + UUID.randomUUID())
                .build());

        categoryRepository.save(Category.builder()
                .categoryName("Desserts-" + UUID.randomUUID())
                .build());
    }

    @Test
    void getAllCategoriesShouldReturnListSortedByName() throws Exception {
        String response = mockMvc.perform(get("/api/categories")
                        .with(user("admin").roles("ADMIN"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Category> categories = objectMapper.readValue(response, new TypeReference<>() {});

        assertThat(categories).isNotEmpty();

        List<Category> sorted = categories.stream()
                .sorted(Comparator.comparing(Category::getCategoryName))
                .toList();

        assertThat(categories).containsExactlyElementsOf(sorted);
    }
}
