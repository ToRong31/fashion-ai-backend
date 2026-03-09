package com.torome.store.order;

import com.torome.store.common.exception.ResourceNotFoundException;
import com.torome.store.order.client.ProductClient;
import com.torome.store.order.client.ProductInfo;
import com.torome.store.order.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;

    public CartService(CartItemRepository cartItemRepository, ProductClient productClient) {
        this.cartItemRepository = cartItemRepository;
        this.productClient = productClient;
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        List<CartItemEntity> items = cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId);
        List<CartItemResponse> responses = items.stream().map(this::toResponse).toList();
        double total = responses.stream().mapToDouble(CartItemResponse::totalPrice).sum();
        return new CartResponse(responses, total);
    }

    @Transactional
    public CartItemResponse addToCart(AddToCartRequest request) {
        // If same product+size already exists, increment quantity
        Optional<CartItemEntity> existing = cartItemRepository
                .findByUserIdOrderByCreatedAtAsc(request.userId())
                .stream()
                .filter(i -> i.getProductId().equals(request.productId())
                          && sameSize(i.getSize(), request.size()))
                .findFirst();

        if (existing.isPresent()) {
            CartItemEntity item = existing.get();
            item.setQuantity(item.getQuantity() + request.quantity());
            return toResponse(cartItemRepository.save(item));
        }

        List<ProductInfo> products = productClient.getProductsByIds(List.of(request.productId()));
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("Product not found: " + request.productId());
        }
        ProductInfo product = products.get(0);

        CartItemEntity item = new CartItemEntity();
        item.setUserId(request.userId());
        item.setProductId(request.productId());
        item.setProductName(product.name());
        item.setPrice(BigDecimal.valueOf(product.price()));
        item.setSize(request.size());
        item.setQuantity(Math.max(request.quantity(), 1));
        item.setCreatedAt(Instant.now());

        return toResponse(cartItemRepository.save(item));
    }

    @Transactional
    public CartItemResponse updateCartItem(Long itemId, UpdateCartItemRequest request) {
        CartItemEntity item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + itemId));

        if (request.quantity() <= 0) {
            cartItemRepository.delete(item);
            return null;
        }

        item.setQuantity(request.quantity());
        if (request.size() != null) {
            item.setSize(request.size());
        }
        return toResponse(cartItemRepository.save(item));
    }

    @Transactional
    public void removeFromCart(Long itemId) {
        CartItemEntity item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + itemId));
        cartItemRepository.delete(item);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteAllByUserId(userId);
    }

    public CartItemResponse toResponse(CartItemEntity item) {
        double totalPrice = item.getPrice().doubleValue() * item.getQuantity();
        return new CartItemResponse(
                item.getId(),
                item.getUserId(),
                item.getProductId(),
                item.getProductName(),
                item.getPrice().doubleValue(),
                item.getSize(),
                item.getQuantity(),
                totalPrice
        );
    }

    private boolean sameSize(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equalsIgnoreCase(b);
    }
}
