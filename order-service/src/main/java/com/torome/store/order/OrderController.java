package com.torome.store.order;

import com.torome.store.order.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @PostMapping("/auto-create")
    public ResponseEntity<AutoCreateOrderResponse> autoCreateOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.autoCreateOrder(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutoCreateOrderResponse> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }
}
