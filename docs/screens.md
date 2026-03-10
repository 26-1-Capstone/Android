# 화면과 사용자 흐름

## 라우트 목록

| 화면 | 라우트 | 핵심 역할 |
| --- | --- | --- |
| 로그인 | `login` | 개발용 로그인 후 토큰 저장 |
| 홈 | `home` | 상품 목록 진입점 |
| 검색 | `search` | 상품 검색 |
| 공동구매 목록 | `groups` | 공동구매 리스트와 필터 |
| 공동구매 생성 | `groups/new` | 새 모집 생성 |
| 공동구매 상세 | `groups/{id}` | 상세 조회와 참여 |
| 상품 상세 | `products/{id}` | 상품 정보, 수량 선택, 장바구니/바로구매 |
| 장바구니 | `cart` | 수량 수정, 삭제, 주문 이동 |
| 주문/결제 | `checkout?...` | 배송지 입력, 주문 생성 |
| 주문 완료 | `orders/{id}/complete` | 주문 완료 확인 |
| 마이페이지 | `mypage` | 프로필, 주문, 참여 내역 |
| 프로필 수정 | `mypage/edit` | 사용자 정보 수정 |

## 대표 흐름

### 로그인

1. `LoginScreen`
2. `ApiService.devLogin()`
3. 토큰을 `AuthStorage`에 저장
4. `home`으로 이동

### 상품 구매

1. `HomeScreen` 또는 `SearchScreen`
2. `ProductDetailScreen`
3. 분기
4. 장바구니 담기: `addToCart()`
5. 바로 구매: `checkout?productId=...&quantity=...`
6. `CheckoutScreen`
7. `createOrder()`
8. `OrderCompleteScreen`

### 장바구니 구매

1. `CartScreen`
2. 선택한 아이템을 `savedStateHandle["checkoutItems"]`에 저장
3. `CheckoutScreen`
4. ViewModel이 `checkoutItems`를 읽어 주문 생성

### 공동구매

1. `GroupListScreen`
2. `GroupDetailScreen`
3. 참여: `joinGroup()`

또는

1. `GroupCreateScreen`
2. 상품 선택 후 모집 생성: `createGroup()`

### 마이페이지

1. `MyPageScreen`
2. 프로필, 주문 내역, 참여 내역 동시 조회
3. `ProfileEditScreen`에서 사용자 정보 수정
4. 로그아웃 시 토큰 삭제 후 `login` 이동

## 화면별 메모

- `LoginScreen`
  - OAuth UI처럼 보이지만 실제 호출은 `devLogin()`
- `CheckoutScreen`
  - 단일 상품 구매와 장바구니 구매 두 경로를 모두 처리
- `MyPageScreen`
  - 탭 전환으로 주문/참여 내역 표시
- `BottomNavBar`
  - 인증이 필요한 탭은 비로그인 상태에서 로그인 화면으로 보냄
