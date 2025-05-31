package org.example.restaurantpos.security;

import org.example.restaurantpos.entity.User;
import org.example.restaurantpos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // Hardcoded admin username & password for easy testing (REMOVE BEFORE PRODUCTION)
    private static final String TEST_ADMIN_USERNAME = "testadmin";
    private static final String TEST_ADMIN_PASSWORD = "testpassword"; // raw password
    private static final String TEST_ADMIN_PASSWORD_HASH = new BCryptPasswordEncoder().encode(TEST_ADMIN_PASSWORD);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Check for hardcoded admin user
        if (TEST_ADMIN_USERNAME.equals(username)) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(TEST_ADMIN_USERNAME)
                    .password(TEST_ADMIN_PASSWORD_HASH)
                    .roles("ADMIN")
                    .build();
        }

        // Otherwise, load from DB as usual
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        System.out.println("Loading user by username: " + username);

        return new CustomUserDetails(user);
    }
}