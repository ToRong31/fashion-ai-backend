# Common Module

Shared utilities and configurations used across all backend services.

## Overview

| Property | Value |
|----------|-------|
| Type | Shared Library (Maven module) |
| Package | `com.torome.store.common` |

## Components

### JWT Authentication

- **JwtService**: Generate and validate JWT tokens
- **JwtAuthFilter**: Spring Security filter for JWT validation

### Exception Handling

- **GlobalExceptionHandler**: Centralized exception handling with `@ControllerAdvice`
- **ResourceNotFoundException**: Standard 404 exception

### Security

- **SecurityConfig**: Spring Security configuration
- **ApiError**: Standard error response format

## JWT Service

### Generate Token
```java
String token = jwtService.generateToken(userId, username);
```

### Validate Token
```java
boolean isValid = jwtService.isValid(token);
Claims claims = jwtService.parseToken(token);
Long userId = Long.parseLong(claims.getSubject());
```

### Configuration
```yaml
app:
  jwt:
    secret: "your-256-bit-secret-key-here"
    expiration-ms: 604800000  # 7 days
```

## Exception Handling

### GlobalExceptionHandler

Catches exceptions and returns standardized error responses:

```json
{
  "status": 404,
  "message": "Resource not found",
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### ResourceNotFoundException
```java
throw new ResourceNotFoundException("Product", "id", productId);
```

## ApiError Response

```json
{
  "status": 400,
  "message": "Invalid request",
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## Security Configuration

### JwtAuthFilter

- Extracts JWT from `Authorization: Bearer <token>` header
- Validates token using JwtService
- Sets Security Context with authenticated user
- Skips validation for `/api/auth/**` endpoints (registration/login)

## Dependencies

- Spring Security
- jjwt (JWT library)
- spring-boot-starter-web

## Usage in Services

Add to service's `pom.xml`:
```xml
<dependency>
    <groupId>com.torome.store</groupId>
    <artifactId>common</artifactId>
    <version>${project.version}</version>
</dependency>
```

## Key Files

- `src/main/java/.../common/config/JwtService.java` - JWT operations
- `src/main/java/.../common/config/JwtAuthFilter.java` - Auth filter
- `src/main/java/.../common/config/SecurityConfig.java` - Security config
- `src/main/java/.../common/exception/GlobalExceptionHandler.java` - Exception handling
- `src/main/java/.../common/exception/ResourceNotFoundException.java` - 404 exception
- `src/main/java/.../common/dto/ApiError.java` - Error response DTO
