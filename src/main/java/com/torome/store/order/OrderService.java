package com.torome.store.order;

import com.torome.store.common.exception.ResourceNotFoundException;
import com.torome.store.order.dto.*;
import com.torome.store.product.ProductEntity;
import com.torome.store.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        List<ProductEntity> products = productRepository.findAllById(request.productIds());

        BigDecimal total = products.stream()
                .map(ProductEntity::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity order = new OrderEntity();
        order.setUserId(request.userId());
        order.setTotalAmount(total);

        for (ProductEntity product : products) {
            order.addItem(new OrderItemEntity(product.getId(), product.getName(), product.getPrice()));
        }

        order = orderRepository.save(order);

        return new CreateOrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalAmount().doubleValue(),
                request.productIds(),
                null
        );
    }

    @Transactional
    public AutoCreateOrderResponse autoCreateOrder(CreateOrderRequest request) {
        List<ProductEntity> products = productRepository.findAllById(request.productIds());

        if (products.isEmpty()) {
            throw new IllegalArgumentException("No valid products found");
        }

        BigDecimal total = products.stream()
                .map(ProductEntity::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity order = new OrderEntity();
        order.setUserId(request.userId());
        order.setTotalAmount(total);

        for (ProductEntity product : products) {
            order.addItem(new OrderItemEntity(product.getId(), product.getName(), product.getPrice()));
        }

        order = orderRepository.save(order);
        order.setVnpayRef("VNPAY-" + order.getId());
        order = orderRepository.save(order);

        List<OrderItemResponse> items = products.stream()
                .map(p -> new OrderItemResponse(p.getId(), p.getName(), p.getPrice().doubleValue()))
                .toList();

        return new AutoCreateOrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalAmount().doubleValue(),
                items,
                order.getVnpayRef()
        );
    }

    @Transactional(readOnly = true)
    public AutoCreateOrderResponse getOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        List<OrderItemResponse> items = order.getItems().stream()
                .map(i -> new OrderItemResponse(i.getProductId(), i.getProductName(), i.getPrice().doubleValue()))
                .toList();

        return new AutoCreateOrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalAmount().doubleValue(),
                items,
                order.getVnpayRef()
        );
    }
}
