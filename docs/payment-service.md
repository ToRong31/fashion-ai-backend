# Payment Service

Handles payment processing via VNPay gateway for Vietnamese e-commerce.

## Overview

| Property | Value |
|----------|-------|
| Port | 9004 |
| Dependencies | Order Service |
| Payment Gateway | VNPay (Vietnam Payment Gateway) |

## Responsibilities

1. **Payment Link Generation**: Generate VNPay payment URLs
2. **Payment Status**: Query payment status from VNPay
3. **Order Update**: Update order with VNPay reference

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/payments/vnpay-gen?orderId={id}` | Generate VNPay payment link |

## Payment Flow

```
1. Order created with status "created"
2. Client requests /api/payments/vnpay-gen?orderId={id}
3. Payment Service generates VNPay URL
4. Client redirects to VNPay
5. User completes payment on VNPay
6. VNPay redirects back to client with result
```

## Request/Response

### Generate Payment Link

**Request**
```
GET /api/payments/vnpay-gen?orderId=123
```

**Response**
```json
{
  "orderId": 123,
  "paymentUrl": "https://sandbox.vnpayment.vn/xxx",
  "vnpayRef": "ORDER123456"
}
```

## VNPay Configuration

| Parameter | Description |
|-----------|-------------|
| `vnp_Version` | Version (2.1.0) |
| `vnp_Command` | Pay |
| `vnp_TmnCode` | Terminal ID |
| `vnp_Amount` | Amount in VND (x100) |
| `vnp_CurrCode` | Currency (VND) |
| `vnp_TxnRef` | Order reference |
| `vnp_OrderInfo` | Order description |
| `vnp_OrderType` | Order type |
| `vnp_Locale` | Language (vn/en) |
| `vnp_ReturnUrl` | Return URL after payment |
| `vnp_SecureHash` | HMAC-SHA256 signature |

## Environment Variables

| Variable | Description |
|----------|-------------|
| `VNPAY_URL` | VNPay gateway URL |
| `VNPAY_TMN_CODE` | Terminal code |
| `VNPAY_HASH_SECRET` | Hash secret key |
| `VNPAY_RETURN_URL` | Return URL after payment |

## Key Files

- `src/main/java/.../payment/PaymentController.java` - REST endpoint
- `src/main/java/.../payment/PaymentService.java` - VNPay integration
- `src/main/java/.../payment/client/OrderClient.java` - Order service client

## Development Notes

- Uses VNPay sandbox for testing
- Payment amount in VND (Vietnamese Dong)
- Amount multiplied by 100 for VNPay API
- Secure hash required for all requests
- Order status updated via Order Service after payment
