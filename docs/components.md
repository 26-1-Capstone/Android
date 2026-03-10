# 공용 UI 컴포넌트

## 레이아웃

- `AppScaffold`
  - 상단 헤더와 하단 네비게이션을 공통으로 묶음
- `AppHeader`
  - 뒤로가기, 검색, 장바구니 진입 제어
- `BottomNavBar`
  - 홈, 공동구매, 장바구니, 마이페이지 탭 제공
  - 인증 필요 탭은 로그인 화면으로 우회

## 입력/상태 표시

- `AddressForm`
  - 배송지/주소 입력 폼
  - 결제 화면, 프로필 수정 화면에서 사용
- `QuantitySelector`
  - 수량 증감
- `NutriProgressBar`
  - 공동구매 달성률 표시
- `StatusBadge`
  - 상태 텍스트 강조
- `LoadingScreen`
  - 전체 로딩 표시
- `EmptyState`
  - 빈 목록 화면

## 카드형 컴포넌트

- `ProductCard`
  - 상품 리스트 카드
- `GroupBuyingCard`
  - 공동구매 카드

## 화면별 재사용 관계

- `HomeScreen`, `SearchScreen` -> `ProductCard`
- `GroupListScreen`, `GroupDetailScreen` -> `GroupBuyingCard`, `NutriProgressBar`
- `CartScreen`, `ProductDetailScreen` -> `QuantitySelector`
- `CheckoutScreen`, `ProfileEditScreen` -> `AddressForm`
