package com.torome.store.user;

import com.torome.store.product.ProductSearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @MockBean
    private ElasticsearchOperations elasticsearchOperations;

    @MockBean
    private ProductSearchRepository productSearchRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setPreferences(Map.of("size", "M", "style", "casual"));
        userRepository.save(user);
    }

    @Test
    void login_success() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"username": "testuser", "password": "password123"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").isNumber())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void login_invalidCredentials() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"username": "testuser", "password": "wrongpassword"}
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUser_success() throws Exception {
        Long userId = userRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.preferences.size").value("M"));
    }

    @Test
    void updateProfile_mergesPreferences() throws Exception {
        Long userId = userRepository.findAll().get(0).getId();

        mockMvc.perform(patch("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                            {"user_id": %d, "preferences": {"favorite_color": "black"}}
                        """, userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferences.size").value("M"))
                .andExpect(jsonPath("$.preferences.favorite_color").value("black"));
    }
}
