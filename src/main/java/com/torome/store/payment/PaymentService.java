package com.torome.store.payment;

import com.torome.store.order.OrderEntity;
import com.torome.store.order.OrderRepository;
import com.torome.store.payment.dto.PaymentLinkResponse;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;

    public PaymentService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public PaymentLinkResponse generatePaymentLink(Long orderId) {
        double amount = orderRepository.findById(orderId)
                .map(o -> o.getTotalAmount().doubleValue())
                .orElse(0.0);

        String paymentUrl = String.format(
                "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?orderId=%d&amount=%.2f&bankCode=NCB",
                orderId, amount
        );

        return new PaymentLinkResponse(orderId, paymentUrl);
    }
}
