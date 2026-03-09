package com.torome.store.order;

import com.torome.store.order.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /** GET /api/cart?userId={id} — list all cart items for a user */
    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    /** POST /api/cart/items — add item to cart (size + quantity) */
    @PostMapping("/items")
    public ResponseEntity<CartItemResponse> addToCart(@Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(request));
    }

    /** PUT /api/cart/items/{id} — update quantity or size of a cart item; quantity=0 removes it */
    @PutMapping("/items/{id}")
    public ResponseEntity<CartItemResponse> updateCartItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartItemResponse response = cartService.updateCartItem(id, request);
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    /** DELETE /api/cart/items/{id} — remove a specific cart item */
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long id) {
        cartService.removeFromCart(id);
        return ResponseEntity.noContent().build();
    }

    /** DELETE /api/cart?userId={id} — clear entire cart for a user */
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestParam("userId") Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
