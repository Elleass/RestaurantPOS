package org.example.restaurantpos.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.restaurantpos.entity.Customer;
import org.example.restaurantpos.repository.CustomerRepository;
import org.junit.jupiter.api.*;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerIntegrationTest extends BaseIntegrationTest{

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CustomerRepository customerRepository;

    private static Long customerId;

    @Test
    @Order(1)
    void createCustomer() throws Exception {
        Customer customer = Customer.builder()
                .name("John Doe")
                .phoneNumber("1234567890")
                .email("john.doe@example.com")
                .build();

        String response = mockMvc.perform(post("/api/customers")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andReturn().getResponse().getContentAsString();

        customerId = objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    @Order(2)
    void getCustomerById() throws Exception {
        mockMvc.perform(get("/api/customers/" + customerId)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId));
    }

    @Test
    @Order(3)
    void updateCustomer() throws Exception {
        Customer updated = Customer.builder()
                .name("John Updated")
                .phoneNumber("0987654321")
                .email("john.updated@example.com")
                .build();

        mockMvc.perform(put("/api/customers/" + customerId)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"));
    }

    @Test
    @Order(4)
    void getAllCustomers() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(5)
    void deleteCustomer() throws Exception {
        mockMvc.perform(delete("/api/customers/" + customerId)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        Assertions.assertFalse(customerRepository.findById(customerId).isPresent());
    }

    @Test
    void unauthorizedUserCannotCreateCustomer() throws Exception {
        Customer customer = Customer.builder()
                .name("Hacker")
                .email("bad@example.com")
                .phoneNumber("000000000")
                .build();

        mockMvc.perform(post("/api/customers")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isForbidden());
    }
}
