# API와 모델

## API 엔드포인트

### 인증

- `GET auth/dev-login`
- `POST auth/reissue`

### 상품

- `GET products`
- `GET products/search?q=...`
- `GET products/{id}`

### 장바구니

- `GET cart`
- `POST cart`
- `PUT cart/{productId}`
- `DELETE cart/{productId}`

### 주문

- `POST orders`

### 공동구매

- `GET groups`
- `GET groups/{id}`
- `POST groups`
- `POST groups/{id}/join`

### 사용자

- `GET users/me`
- `PUT users/me`
- `GET users/me/orders`
- `GET users/me/participations`

## 주요 모델

### 공통 응답

- `ApiResponse<T>`
  - `success`
  - `data`
  - `message`
- `PageResponse<T>`
  - `content`
  - `totalElements`
  - `totalPages`
  - `last`

### 상품/공동구매

- `Product`
- `Group`

### 장바구니/주문

- `CartItem`
- `CartResponse`
- `Order`
- `OrderItem`
- `CreateOrderRequest`
- `ShippingAddress`
- `ResourceIdResponse`

### 사용자

- `User`
- `Address`
- `UpdateProfileRequest`
- `Participation`

### 요청 모델

- `CreateGroupRequest`
- `JoinGroupRequest`
- `AddToCartRequest`
- `UpdateCartRequest`

## 네트워크 클라이언트 요약

- 기본 주소: `http://10.0.2.2:8080/api/v1/`
- `Authorization: Bearer {token}` 자동 추가
- `401` 응답 시 `auth/reissue`로 토큰 재발급 시도
- 로깅 인터셉터 활성화

## 모델 연결 예시

- 홈/검색/상세: `Product`
- 장바구니: `CartResponse` -> `CartItem`
- 결제: `CheckoutItem` -> `CreateOrderRequest`
- 마이페이지: `User`, `Order`, `Participation`
