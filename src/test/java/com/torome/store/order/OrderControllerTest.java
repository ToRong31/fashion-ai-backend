package com.torome.store.order;

import com.torome.store.product.ProductEntity;
import com.torome.store.product.ProductRepository;
import com.torome.store.product.ProductSearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.torome.store.user.UserEntity;
import com.torome.store.user.UserRepository;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @MockBean
    private ElasticsearchOperations elasticsearchOperations;

    @MockBean
    private ProductSearchRepository productSearchRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Long userId;
    private Long productId;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity user = new UserEntity("ordertest", "hashed");
        userRepository.save(user);
        userId = user.getId();

        ProductEntity product = new ProductEntity();
        product.setName("Test Product");
        product.setDescription("A test product");
        product.setPrice(new BigDecimal("49.99"));
        product.setStockQuantity(10);
        product.setMetadata(Map.of("category", "tops"));
        productRepository.save(product);
        productId = product.getId();
    }

    @Test
    void autoCreateOrder_success() throws Exception {
        mockMvc.perform(post("/api/orders/auto-create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                            {"user_id": %d, "product_ids": [%d]}
                        """, userId, productId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("created"))
                .andExpect(jsonPath("$.total_amount").value(49.99))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].name").value("Test Product"))
                .andExpect(jsonPath("$.vnpay_ref").isString());
    }

    @Test
    void autoCreateOrder_noValidProducts() throws Exception {
        mockMvc.perform(post("/api/orders/auto-create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                            {"user_id": %d, "product_ids": [9999]}
                        """, userId)))
                .andExpect(status().isBadRequest());
    }
}
