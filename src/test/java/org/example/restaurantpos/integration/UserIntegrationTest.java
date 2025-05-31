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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("integration-test")
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserIntegrationTest extends BaseIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;

    private static Long userId;

    @BeforeAll
    static void setup() {
        System.out.println("Starting UserIntegrationTest");
    }

    @BeforeEach
    void setupEachTest() {
        // Reset database using SQL script instead of repository methods
        System.out.println("Setting up test data");
    }

    @Test
    @Order(1)
    @Sql(scripts = {"/sql/truncate_all_tables.sql", "/sql/create_roles.sql"})
    void createUser() throws Exception {
        System.out.println("Running createUser test");

        // Get the existing role
        Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN")
                .orElseThrow(() -> new IllegalStateException("Admin role not found"));

        User user = User.builder()
                .username("integration_user")
                .password("pass123")
                .isLocked(false)
                .role(adminRole)
                .build();

        String response = mockMvc.perform(post("/api/users")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("integration_user"))
                .andReturn().getResponse().getContentAsString();

        userId = objectMapper.readTree(response).get("id").asLong();
        System.out.println("Created user with ID: " + userId);
    }

    @Test
    @Order(2)
    @Sql(scripts = {"/sql/truncate_all_tables.sql", "/sql/create_roles.sql", "/sql/create_test_user.sql"})
    void getUserById() throws Exception {
        System.out.println("Running getUserById test");

        // Get ID of pre-created user from SQL script
        userId = userRepository.findByUsername("test_user")
                .orElseThrow(() -> new IllegalStateException("Test user not found"))
                .getId();

        mockMvc.perform(get("/api/users/" + userId)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));
    }

    @Test
    @Order(3)
    @Sql(scripts = {"/sql/truncate_all_tables.sql", "/sql/create_roles.sql", "/sql/create_test_user.sql"})
    void updateUser() throws Exception {
        System.out.println("Running updateUser test");

        // Get ID of pre-created user from SQL script
        userId = userRepository.findByUsername("test_user")
                .orElseThrow(() -> new IllegalStateException("Test user not found"))
                .getId();

        Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN")
                .orElseThrow(() -> new IllegalStateException("Admin role not found"));

        User updated = User.builder()
                .username("updated_user")
                .password("newpass")
                .isLocked(true)
                .role(adminRole)
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
    @Sql(scripts = {"/sql/truncate_all_tables.sql", "/sql/create_roles.sql", "/sql/create_test_user.sql"})
    void getAllUsers() throws Exception {
        System.out.println("Running getAllUsers test");

        mockMvc.perform(get("/api/users")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(5)
    @Sql(scripts = {"/sql/truncate_all_tables.sql", "/sql/create_roles.sql", "/sql/create_test_user.sql"})
    void deleteUser() throws Exception {
        System.out.println("Running deleteUser test");

        // Get ID of pre-created user from SQL script
        userId = userRepository.findByUsername("test_user")
                .orElseThrow(() -> new IllegalStateException("Test user not found"))
                .getId();

        mockMvc.perform(delete("/api/users/" + userId)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        Optional<User> deleted = userRepository.findById(userId);
        Assertions.assertTrue(deleted.isEmpty());
    }

    @Test
    @Sql(scripts = {"/sql/truncate_all_tables.sql", "/sql/create_roles.sql", "/sql/create_test_user.sql"})
    void userWithoutAdminRoleCannotAccess() throws Exception {
        System.out.println("Running permission test");

        mockMvc.perform(get("/api/users")
                        .with(user("basic_user").roles("USER")))
                .andExpect(status().isForbidden());
    }
}