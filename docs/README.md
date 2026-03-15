# ToRoMe Backend Services

Backend microservices for Vietnamese fashion e-commerce platform with AI assistant.

## Architecture

```
                                    ┌─────────────────┐
                                    │  Gateway (:9000) │
                                    └────────┬────────┘
                                             │
          ┌──────────────┬───────────────────┼───────────────────┬──────────────┐
          │              │                   │                   │              │
    ┌─────▼─────┐  ┌─────▼─────┐     ┌─────▼─────┐     ┌─────▼─────┐  ┌─────▼─────┐
    │   User    │  │  Product  │     │   Order   │     │  Payment  │  │    AI     │
    │ :9001     │  │ :9002     │     │ :9003     │     │ :9004     │  │ :8000     │
    └───────────┘  └─────┬─────┘     └─────┬─────┘     └───────────┘  └─────┬─────┘
                        │                 │                                 │
                  ┌─────▼─────┐     ┌─────▼─────┐                           │
                  │Elasticsearch│     │Product   │                           │
                  │  :9200     │     │Service   │                           │
                  └───────────┘     └──────────┘                           │
                        │                                                    │
                  ┌─────▼───────────────────────────────────────────────┐
                  │              PostgreSQL (:5432)                      │
                  │  torome_store                                        │
                  └──────────────────────────────────────────────────────┘
```

## Services

| Service | Port | Purpose |
|---------|------|---------|
| [Gateway Service](./gateway-service.md) | 9000 | API Gateway, routes requests to backend services + proxies AI |
| [User Service](./user-service.md) | 9001 | Authentication, user profile, preferences |
| [Product Service](./product-service.md) | 9002 | Product catalog, vector search via Elasticsearch |
| [Order Service](./order-service.md) | 9003 | Order management, shopping cart |
| [Payment Service](./payment-service.md) | 9004 | VNPay integration |

## Infrastructure

- **PostgreSQL** (5432): Primary database (`torome_store`)
- **Elasticsearch** (9200): Vector search for semantic product discovery

## Technology Stack

- Spring Boot 3.4, Java 21
- Spring Cloud Gateway for API routing
- PostgreSQL 16 with Flyway migrations
- Elasticsearch 8.17 for vector search
- JWT authentication

## Quick Start

```bash
cd fashion-ai-backend
docker compose up --build
```

Wait for `Started StoreApplication` message. Services available at:
- Gateway: http://localhost:9000
- User Service: http://localhost:9001
- Product Service: http://localhost:9002
- Order Service: http://localhost:9003
- Payment Service: http://localhost:9004
- Elasticsearch: http://localhost:9200
- PostgreSQL: localhost:5432

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_URL` | PostgreSQL connection URL | `jdbc:postgresql://postgres:5432/torome_store` |
| `DB_USERNAME` | Database username | `torome` |
| `DB_PASSWORD` | Database password | `torome123` |
| `JWT_SECRET` | JWT signing key | Development default (change in production) |
| `SPRING_PROFILES_ACTIVE` | Spring profile | `dev` |
| `ELASTICSEARCH_URI` | Elasticsearch URL | `http://localhost:9200` |

## API Endpoints Summary

| Method | Path | Service | Description |
|--------|------|---------|-------------|
| POST | `/api/auth/register` | User | Register new user |
| POST | `/api/auth/login` | User | Login, returns JWT |
| GET | `/api/users/{id}` | User | Get user profile |
| PATCH | `/api/users/profile` | User | Update preferences |
| GET | `/api/products` | Product | List all products |
| GET | `/api/products/{id}` | Product | Get product details |
| POST | `/api/products/vector-search` | Product | Semantic search |
| POST | `/api/products/batch` | Product | Get products by IDs |
| GET | `/api/cart` | Order | Get user's cart |
| POST | `/api/cart/items` | Order | Add to cart |
| PUT | `/api/cart/items/{id}` | Order | Update cart item |
| DELETE | `/api/cart/items/{id}` | Order | Remove from cart |
| POST | `/api/orders` | Order | Create order |
| POST | `/api/orders/auto-create` | Order | Create order from cart |
| POST | `/api/orders/checkout` | Order | Checkout from cart |
| GET | `/api/payments/vnpay-gen` | Payment | Generate VNPay link |
| POST | `/api/ai/chat` | Gateway | Chat with AI orchestrator |

## Shared Components

- [Common Module](./common.md): JWT, exceptions, security configurations
