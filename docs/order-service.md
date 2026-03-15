# Order Service

Handles order creation, shopping cart management, and integrates with product service for pricing.

## Overview

| Property | Value |
|----------|-------|
| Port | 9003 |
| Database | PostgreSQL (`orders`, `order_items`, `cart_items` tables) |
| Dependencies | Product Service (for product info/pricing) |

## Responsibilities

1. **Shopping Cart**: Add, update, remove cart items with size support
2. **Order Creation**: Create orders from cart or direct request
3. **Order Management**: View order details, track order status
4. **Checkout**: Convert cart to order with pricing from product service

## API Endpoints

### Cart

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/cart?userId={id}` | Get user's cart |
| POST | `/api/cart/items` | Add item to cart |
| PUT | `/api/cart/items/{id}` | Update item quantity/size |
| DELETE | `/api/cart/items/{id}` | Remove item from cart |
| DELETE | `/api/cart?userId={id}` | Clear entire cart |

### Orders

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/orders directly` | Create order |
| POST | `/api/orders/auto-create` | Create order from cart |
| POST | `/api/orders/checkout` | Checkout cart, create order + payment |
| GET | `/api/orders/{id}` | Get order by ID |

## Data Model

### Orders Table

```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    status VARCHAR(20) NOT NULL DEFAULT 'created',
    vnpay_ref VARCHAR(100),
    total_amount NUMERIC(10, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_orders_user_id ON orders(user_id);
```

### Order Items Table

```sql
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    product_name VARCHAR(255) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    size VARCHAR(20)
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
```

### Cart Items Table

```sql
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    product_id BIGINT NOT NULL REFERENCES products(id),
    product_name VARCHAR(255) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    size VARCHAR(20),
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cart_items_user_id ON cart_items(user_id);
```

## Request/Response DTOs

### AddToCartRequest
```json
{
  "userId": 1,
  "productId": 5,
  "size": "M",
  "quantity": 2
}
```

### UpdateCartItemRequest
```json
{
  "size": "L",
  "quantity": 3
}
```

### CartItemResponse
```json
{
  "id": 1,
  "productId": 5,
  "productName": "Classic Black T-Shirt",
  "price": 29.99,
  "size": "M",
  "quantity": 2,
  "subtotal": 59.98
}
```

### CartResponse
```json
{
  "userId": 1,
  "items": [...],
  "totalAmount": 59.98
}
```

### CreateOrderRequest
```json
{
  "userId": 1,
  "items": [
    {
      "productId": 5,
      "size": "M",
      "quantity": 2
    }
  ]
}
```

### AutoCreateOrderResponse
```json
{
  "orderId": 1,
  "userId": 1,
  "status": "created",
  "items": [...],
  "totalAmount": 59.98,
  "vnpayRef": null,
  "createdAt": "2024-01-01T00:00:00Z"
}
```

## Order Status Flow

```
created → pending → confirmed → shipped → delivered → completed
         ↓
      cancelled
```

## Checkout Flow

1. Get user's cart items
2. Verify product availability via Product Service
3. Calculate total amount
4. Create order with items
5. Clear cart
6. Return order with `vnpayRef` for payment link

## Key Files

- `src/main/java/.../order/OrderController.java` - Order endpoints
- `src/main/java/.../order/CartController.java` - Cart endpoints
- `src/main/java/.../order/OrderService.java` - Order business logic
- `src/main/java/.../order/CartService.java` - Cart business logic
- `src/main/java/.../order/client/ProductClient.java` - Product service client

## Development Notes

- Cart is persistent (stored in database, not session)
- Size is mandatory for cart items
- Order total calculated from product prices at time of creation
- Product Service called to get current prices and verify stock
