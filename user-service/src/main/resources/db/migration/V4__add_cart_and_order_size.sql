-- Cart items table for persistent shopping cart
CREATE TABLE cart_items (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT        NOT NULL REFERENCES users(id),
    product_id   BIGINT        NOT NULL REFERENCES products(id),
    product_name VARCHAR(255)  NOT NULL,
    price        NUMERIC(10,2) NOT NULL,
    size         VARCHAR(20),
    quantity     INT           NOT NULL DEFAULT 1,
    created_at   TIMESTAMPTZ   DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cart_items_user_id ON cart_items(user_id);

-- Add size column to order_items to track selected size per line
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS size VARCHAR(20);
