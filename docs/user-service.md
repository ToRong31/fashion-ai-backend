# User Service

Handles user authentication, registration, and profile management with preferences storage.

## Overview

| Property | Value |
|----------|-------|
| Port | 9001 |
| Database | PostgreSQL (`users` table) |
| Authentication | JWT (JSON Web Tokens) |

## Responsibilities

1. **User Registration**: Create new user accounts with username/password
2. **Authentication**: Login with credentials, issue JWT tokens
3. **Profile Management**: Get/update user profile including preferences
4. **Preferences Storage**: Store user preferences (size, color, style) as JSONB

## API Endpoints

### Authentication

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login, returns JWT token |

### User Profile

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/users/{id}` | Get user by ID |
| PATCH | `/api/users/profile` | Update user preferences |

## Data Model

### Users Table

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    preferences JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
```

### Preferences Schema (JSONB)

```json
{
  "size": "M",
  "color": "black",
  "style": "minimal"
}
```

## Request/Response DTOs

### RegisterRequest
```json
{
  "username": "string",
  "password": "string"
}
```

### LoginRequest
```json
{
  "username": "string",
  "password": "string"
}
```

### LoginResponse
```json
{
  "token": "jwt-token-string",
  "userId": 1,
  "username": "string"
}
```

### UserResponse
```json
{
  "id": 1,
  "username": "string",
  "preferences": {
    "size": "M",
    "color": "black",
    "style": "minimal"
  },
  "createdAt": "2024-01-01T00:00:00Z"
}
```

### UpdateProfileRequest
```json
{
  "preferences": {
    "size": "L",
    "color": "navy",
    "style": "smart-casual"
  }
}
```

## JWT Authentication

- **Algorithm**: HMAC-SHA256
- **Token includes**: `userId` (subject), `username` (claim)
- **Default expiration**: 7 days (configurable)

## Security

- Passwords hashed with BCrypt
- JWT validated by `JwtAuthFilter` in common module
- User ID extracted from token for authenticated requests

## Test Accounts

| Username | Password | Preferences |
|----------|----------|-------------|
| demo_user | demo123 | size M, black, minimal |
| fashion_lover | demo123 | size L, navy, smart-casual |

## Key Files

- `src/main/java/.../user/AuthController.java` - Auth endpoints
- `src/main/java/.../user/UserController.java` - Profile endpoints
- `src/main/java/.../user/UserService.java` - Business logic
- `src/main/java/.../user/UserEntity.java` - JPA entity
- `src/main/resources/db/migration/V1__create_schema.sql` - Schema
