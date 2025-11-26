# Loyalty Service API Documentation

This document provides information about the Loyalty Service API authentication requirements.

## API Key Authentication

Some endpoints in the Loyalty Service require API KEY authentication. These are endpoints that should only be called by external services (e.g., Bill Service).

### Protected Endpoints

The following endpoints require an API KEY header:

1. **Add Loyalty Points** - `POST /api/loyalty/points`
2. **Redeem Coupon** - `POST /api/loyalty/coupons/redeem`

### Authentication Header

All requests to protected endpoints must include the `X-API-KEY` header:

```
X-API-KEY: your-secret-api-key
```

### Configuration

The API key is configured through the environment variable `LOYALTY_API_KEY`:

```bash
# In .env file
LOYALTY_API_KEY=your-secret-api-key-change-in-production
```

If not set, it defaults to `default-loyalty-api-key-change-me` (configured in `application.properties`).

### Example Requests

#### Add Loyalty Points

```bash
curl -X POST http://2306240080-be.hafizmuh.site/api/loyalty/points \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: your-secret-api-key" \
  -d '{
    "customerId": "123e4567-e89b-12d3-a456-426614174000",
    "points": 100,
    "reference": "ORDER-123"
  }'
```

**Success Response (200 OK):**
```json
{
  "status": 200,
  "data": {
    "customerId": "123e4567-e89b-12d3-a456-426614174000",
    "points": 250
  },
  "message": "Points added successfully",
  "timestamp": "2025-11-26T21:20:00.000+00:00"
}
```

**Error Response - Missing API Key (401 Unauthorized):**
```json
{
  "status": 401,
  "message": "API key is required",
  "timestamp": "2025-11-26T21:20:00.000+00:00"
}
```

**Error Response - Invalid API Key (401 Unauthorized):**
```json
{
  "status": 401,
  "message": "Invalid API key",
  "timestamp": "2025-11-26T21:20:00.000+00:00"
}
```

#### Redeem Coupon

```bash
curl -X POST http://2306240080-be.hafizmuh.site/api/loyalty/coupons/redeem \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: your-secret-api-key" \
  -d '{
    "code": "SUMMER-JOHN-1",
    "customerId": "123e4567-e89b-12d3-a456-426614174000"
  }'
```

**Success Response (200 OK):**
```json
{
  "status": 200,
  "data": {
    "code": "SUMMER-JOHN-1",
    "customerId": "123e4567-e89b-12d3-a456-426614174000",
    "couponId": "987e6543-e21c-12d3-a456-426614174999",
    "percentOff": 15,
    "valid": true
  },
  "message": "Coupon redeemed successfully",
  "timestamp": "2025-11-26T21:20:00.000+00:00"
}
```

**Error Response - Invalid Coupon (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Coupon code INVALID-CODE not found",
  "timestamp": "2025-11-26T21:20:00.000+00:00"
}
```

**Error Response - Already Used (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Coupon has already been used",
  "timestamp": "2025-11-26T21:20:00.000+00:00"
}
```

### Public Endpoints

The following endpoints do NOT require API KEY authentication and can be called by any authenticated user:

- `GET /api/loyalty/coupons/available` - Get all available coupons
- `GET /api/loyalty/customers/{customerId}/balance` - Get customer loyalty points balance
- `GET /api/loyalty/customers/{customerId}/dashboard` - Get customer loyalty dashboard
- `POST /api/loyalty/coupons/purchase` - Purchase a coupon (requires customer authentication)
- `GET /api/loyalty/customers/{customerId}/purchased-coupons` - Get purchased coupons

## Integration with Bill Service

The Bill Service should integrate with the Loyalty Service as follows:

### When Customer Makes a Payment

```java
// Example integration code for Bill Service
String loyaltyApiKey = System.getenv("LOYALTY_API_KEY");
HttpHeaders headers = new HttpHeaders();
headers.set("X-API-KEY", loyaltyApiKey);
headers.setContentType(MediaType.APPLICATION_JSON);

AddPointsRequest request = new AddPointsRequest();
request.setCustomerId(customerId);
request.setPoints(calculatePoints(totalAmount)); // e.g., 1 point per 10000 IDR
request.setReference(orderId);

HttpEntity<AddPointsRequest> entity = new HttpEntity<>(request, headers);
ResponseEntity<BaseResponse<LoyaltyPointsResponse>> response = 
    restTemplate.postForEntity(
        loyaltyServiceUrl + "/api/loyalty/points",
        entity,
        new ParameterizedTypeReference<BaseResponse<LoyaltyPointsResponse>>() {}
    );
```

### When Customer Uses a Coupon Code

```java
// Example integration code for Bill Service
String loyaltyApiKey = System.getenv("LOYALTY_API_KEY");
HttpHeaders headers = new HttpHeaders();
headers.set("X-API-KEY", loyaltyApiKey);
headers.setContentType(MediaType.APPLICATION_JSON);

RedeemCouponRequest request = new RedeemCouponRequest();
request.setCode(couponCode);
request.setCustomerId(customerId);

HttpEntity<RedeemCouponRequest> entity = new HttpEntity<>(request, headers);
ResponseEntity<BaseResponse<RedeemCouponResponse>> response = 
    restTemplate.postForEntity(
        loyaltyServiceUrl + "/api/loyalty/coupons/redeem",
        entity,
        new ParameterizedTypeReference<BaseResponse<RedeemCouponResponse>>() {}
    );

if (response.getStatusCode().is2xxSuccessful()) {
    int percentOff = response.getBody().getData().getPercentOff();
    // Apply discount: discountAmount = totalAmount * percentOff / 100
}
```

## Security Considerations

1. **Keep API Key Secret**: Never commit the actual API key to version control
2. **Use Environment Variables**: Always configure the API key through environment variables
3. **Rotate Keys**: Periodically rotate the API key, especially if compromised
4. **HTTPS Only**: In production, always use HTTPS to prevent API key interception
5. **Monitor Usage**: Log all API key usage for audit purposes
