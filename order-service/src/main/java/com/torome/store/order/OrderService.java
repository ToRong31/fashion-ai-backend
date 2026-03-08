package com.torome.store.order;

import com.torome.store.common.exception.ResourceNotFoundException;
import com.torome.store.order.client.ProductClient;
import com.torome.store.order.client.ProductInfo;
import com.torome.store.order.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public OrderService(OrderRepository orderRepository, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        List<ProductInfo> products = productClient.getProductsByIds(request.productIds());

        BigDecimal total = products.stream()
                .map(p -> BigDecimal.valueOf(p.price()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity order = new OrderEntity();
        order.setUserId(request.userId());
        order.setTotalAmount(total);

        for (ProductInfo product : products) {
            order.addItem(new OrderItemEntity(product.id(), product.name(), BigDecimal.valueOf(product.price())));
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
        List<ProductInfo> products = productClient.getProductsByIds(request.productIds());

        if (products.isEmpty()) {
            throw new IllegalArgumentException("No valid products found");
        }

        BigDecimal total = products.stream()
                .map(p -> BigDecimal.valueOf(p.price()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity order = new OrderEntity();
        order.setUserId(request.userId());
        order.setTotalAmount(total);

        for (ProductInfo product : products) {
            order.addItem(new OrderItemEntity(product.id(), product.name(), BigDecimal.valueOf(product.price())));
        }

        order = orderRepository.save(order);
        order.setVnpayRef("VNPAY-" + order.getId());
        order = orderRepository.save(order);

        List<OrderItemResponse> items = products.stream()
                .map(p -> new OrderItemResponse(p.id(), p.name(), p.price()))
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
