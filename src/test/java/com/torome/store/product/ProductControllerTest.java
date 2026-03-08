package com.torome.store.product;

import com.torome.store.product.dto.ProductListResponse;
import com.torome.store.product.dto.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ElasticsearchOperations elasticsearchOperations;

    @MockBean
    private ProductSearchRepository productSearchRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        ProductEntity product = new ProductEntity();
        product.setName("Test Black Jacket");
        product.setDescription("A test black jacket for formal occasions");
        product.setPrice(new BigDecimal("89.99"));
        product.setStockQuantity(10);
        product.setMetadata(Map.of(
                "category", "outerwear",
                "color", "black",
                "style", "formal"
        ));
        productRepository.save(product);
    }

    @Test
    void listProducts_returnsAllProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products").isArray())
                .andExpect(jsonPath("$.products.length()").value(1))
                .andExpect(jsonPath("$.products[0].name").value("Test Black Jacket"));
    }

    @Test
    void getProduct_returnsProduct() throws Exception {
        Long id = productRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Black Jacket"))
                .andExpect(jsonPath("$.price").value(89.99));
    }

    @Test
    void getProduct_notFound() throws Exception {
        mockMvc.perform(get("/api/products/9999"))
                .andExpect(status().isNotFound());
    }
}
