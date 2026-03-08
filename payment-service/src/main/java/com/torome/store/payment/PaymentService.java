package com.torome.store.payment;

import com.torome.store.payment.client.OrderClient;
import com.torome.store.payment.dto.PaymentLinkResponse;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final OrderClient orderClient;

    public PaymentService(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    public PaymentLinkResponse generatePaymentLink(Long orderId) {
        double amount = orderClient.getOrderAmount(orderId);

        String paymentUrl = String.format(
                "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?orderId=%d&amount=%.2f&bankCode=NCB",
                orderId, amount
        );

        return new PaymentLinkResponse(orderId, paymentUrl);
    }
}
