package com.torome.store.payment;

import com.torome.store.order.OrderEntity;
import com.torome.store.order.OrderRepository;
import com.torome.store.product.ProductSearchRepository;
import com.torome.store.user.UserEntity;
import com.torome.store.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

    @MockBean
    private ElasticsearchOperations elasticsearchOperations;

    @MockBean
    private ProductSearchRepository productSearchRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private Long orderId;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity user = new UserEntity("paytest", "hashed");
        userRepository.save(user);

        OrderEntity order = new OrderEntity();
        order.setUserId(user.getId());
        order.setTotalAmount(new BigDecimal("134.99"));
        orderRepository.save(order);
        orderId = order.getId();
    }

    @Test
    void generatePaymentLink_success() throws Exception {
        mockMvc.perform(get("/api/payments/vnpay-gen")
                        .param("orderId", orderId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order_id").value(orderId))
                .andExpect(jsonPath("$.payment_url").value(
                        org.hamcrest.Matchers.containsString("vnpayment.vn")));
    }
}
