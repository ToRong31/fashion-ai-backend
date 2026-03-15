# Gateway Service

Routes incoming requests to appropriate backend microservices and proxies AI requests to the AI Orchestrator.

## Overview

| Property | Value |
|----------|-------|
| Port | 9000 |
| Type | Spring Cloud Gateway |
| Dependencies | All backend services |

## Responsibilities

1. **Request Routing**: Route `/api/auth/*` → User Service, `/api/products/*` → Product Service, etc.
2. **AI Proxy**: Forward `/api/ai/*` requests to AI Orchestrator (port 8000) with path stripping
3. **CORS**: Enable CORS for all origins
4. **Health Check**: Provide `/health` endpoint

## Routes Configuration

| Path Pattern | Destination Service | URL |
|--------------|---------------------|-----|
| `/api/auth/**` | user-service | `http://localhost:9001` |
| `/api/users/**` | user-service | `http://localhost:9001` |
| `/api/products/**` | product-service | `http://localhost:9002` |
| `/api/orders/**` | order-service | `http://localhost:9003` |
| `/api/cart/**` | order-service | `http://localhost:9003` |
| `/api/payments/**` | payment-service | `http://localhost:9004` |
| `/api/ai/**` | AI Orchestrator | `http://localhost:8000` (strips `/api` prefix) |

## CORS Configuration

```yaml
allowed-origin-patterns: "*"
allowed-methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
allowed-headers: "*"
expose-headers: "*"
max-age: 3600
```

## Environment Variables

| Variable | Description |
|----------|-------------|
| `USER_SERVICE_URL` | User service URL (default: `http://localhost:9001`) |
| `PRODUCT_SERVICE_URL` | Product service URL (default: `http://localhost:9002`) |
| `ORDER_SERVICE_URL` | Order service URL (default: `http://localhost:9003`) |
| `PAYMENT_SERVICE_URL` | Payment service URL (default: `http://localhost:9004`) |
| `AI_ORCHESTRATOR_URL` | AI orchestrator URL (default: `http://host.docker.internal:8000`) |

## Key Files

- `src/main/resources/application.yml` - Route configuration
- `src/main/java/.../gateway/HealthRouteConfig.java` - Health endpoint
- `src/main/java/.../gateway/GatewayApplication.java` - Application entry

## Development Notes

- Gateway is the **only** entry point for frontend clients
- Frontend should call `http://localhost:9000` only, never directly to other services
- AI requests strip `/api` prefix before forwarding (e.g., `/api/ai/chat` → `/chat`)
