package com.torome.store.payment;

import com.torome.store.payment.dto.PaymentLinkResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/vnpay-gen")
    public ResponseEntity<PaymentLinkResponse> generatePaymentLink(@RequestParam("orderId") Long orderId) {
        return ResponseEntity.ok(paymentService.generatePaymentLink(orderId));
    }
}
