package org.example.restaurantpos.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.restaurantpos.entity.Role;
import org.example.restaurantpos.entity.User;
import org.example.restaurantpos.repository.RoleRepository;
import org.example.restaurantpos.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"spring.profiles.active=integration-test"})
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthIntegrationTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
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
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeAll
    void setupUser() {
        userRepository.deleteAll(); // 🔥 clear DB to ensure clean state

        Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(
                        Role.builder().roleName("ROLE_ADMIN").description("Administrator").build()
                ));

        String password = "adminpassword";
        String encoded = passwordEncoder.encode(password);

        System.out.println("Saving user with password: " + password);
        System.out.println("Encoded password: " + encoded);

        userRepository.save(User.builder()
                .username("admin")
                .password(encoded)
                .isLocked(false)
                .role(adminRole)
                .build());
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void loginWithValidCredentials() throws Exception {
        Map<String, String> credentials = Map.of(
                "username", "admin",
                "password", "adminpassword"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void loginWithInvalidCredentials() throws Exception {
        Map<String, String> credentials = Map.of(
                "username", "wronguser",
                "password", "wrongpass"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized());
    }
}
