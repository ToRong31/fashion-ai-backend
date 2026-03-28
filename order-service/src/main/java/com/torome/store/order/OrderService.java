package com.torome.store.order;

import com.torome.store.common.exception.ResourceNotFoundException;
import com.torome.store.order.client.ProductClient;
import com.torome.store.order.client.ProductInfo;
import com.torome.store.order.client.UserClient;
import com.torome.store.order.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final UserClient userClient;
    private final CartItemRepository cartItemRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductClient productClient,
                        UserClient userClient,
                        CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.userClient = userClient;
        this.cartItemRepository = cartItemRepository;
    }

    /**
     * Validates user and all product IDs exist before placing an order.
     * User is validated via REST call to user-service.
     * Products are validated via REST call to product-service.
     * Throws IllegalArgumentException if any reference is invalid.
     */
    private void validateOrderReferences(Long userId, List<Long> productIds) {
        if (!userClient.userExists(userId)) {
            throw new IllegalArgumentException("User does not exist: " + userId);
        }
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Product list cannot be empty");
        }
        List<ProductInfo> products = productClient.getProductsByIds(productIds);
        if (products.size() != productIds.size()) {
            throw new IllegalArgumentException("One or more products not found");
        }
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        validateOrderReferences(request.userId(), request.productIds());

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
        validateOrderReferences(request.userId(), request.productIds());

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
                .map(p -> new OrderItemResponse(p.id(), p.name(), p.price(), 1, null))
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
                .map(i -> new OrderItemResponse(
                        i.getProductId(), i.getProductName(),
                        i.getPrice().doubleValue(), i.getQuantity(), i.getSize()))
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

    /**
     * Creates an order from all cart items of the given user,
     * assigns a VNPay reference, clears the cart, and returns the order.
     */
    @Transactional
    public AutoCreateOrderResponse checkoutFromCart(Long userId) {
        if (!userClient.userExists(userId)) {
            throw new IllegalArgumentException("User does not exist: " + userId);
        }

        List<CartItemEntity> cartItems = cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        BigDecimal total = cartItems.stream()
                .map(c -> c.getPrice().multiply(BigDecimal.valueOf(c.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity order = new OrderEntity();
        order.setUserId(userId);
        order.setTotalAmount(total);

        for (CartItemEntity cartItem : cartItems) {
            OrderItemEntity item = new OrderItemEntity(
                    cartItem.getProductId(), cartItem.getProductName(), cartItem.getPrice());
            item.setQuantity(cartItem.getQuantity());
            item.setSize(cartItem.getSize());
            order.addItem(item);
        }

        order = orderRepository.save(order);
        order.setVnpayRef("VNPAY-" + order.getId());
        order = orderRepository.save(order);

        // Clear the cart after order is persisted
        cartItemRepository.deleteAllByUserId(userId);

        List<OrderItemResponse> responseItems = order.getItems().stream()
                .map(i -> new OrderItemResponse(
                        i.getProductId(), i.getProductName(),
                        i.getPrice().doubleValue(), i.getQuantity(), i.getSize()))
                .toList();

        return new AutoCreateOrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalAmount().doubleValue(),
                responseItems,
                order.getVnpayRef()
        );
    }
}
