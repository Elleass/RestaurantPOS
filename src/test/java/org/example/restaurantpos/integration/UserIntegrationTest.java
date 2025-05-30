package org.example.restaurantpos.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.restaurantpos.entity.Role;
import org.example.restaurantpos.entity.User;
import org.example.restaurantpos.repository.RoleRepository;
import org.example.restaurantpos.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"spring.profiles.active=integration-test"})
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserIntegrationTest {

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
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;

    private static Long userId;

    @Test
    @Order(1)
    void createUser() throws Exception {
        Role role = roleRepository.findByRoleName("ROLE_ADMIN")
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN must exist in the database"));

        User user = User.builder()
                .username("integration_user")
                .password("pass123")
                .isLocked(false)
                .role(role)
                .build();

        String response = mockMvc.perform(post("/api/users")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("integration_user"))
                .andReturn().getResponse().getContentAsString();

        userId = objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    @Order(2)
    void getUserById() throws Exception {
        mockMvc.perform(get("/api/users/" + userId)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));
    }

    @Test
    @Order(3)
    void updateUser() throws Exception {
        Role role = roleRepository.findByRoleName("ROLE_ADMIN")
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN must exist in the database"));

        User updated = User.builder()
                .username("updated_user")
                .password("newpass")
                .isLocked(true)
                .role(role)
                .build();

        mockMvc.perform(put("/api/users/" + userId)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updated_user"))
                .andExpect(jsonPath("$.isLocked").value(true));
    }

    @Test
    @Order(4)
    void getAllUsers() throws Exception {
        mockMvc.perform(get("/api/users")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(5)
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/" + userId)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        Optional<User> deleted = userRepository.findById(userId);
        Assertions.assertTrue(deleted.isEmpty());
    }

    @Test
    void userWithoutAdminRoleCannotAccess() throws Exception {
        mockMvc.perform(get("/api/users")
                        .with(user("basic_user").roles("USER")))
                .andExpect(status().isForbidden());
    }
}
