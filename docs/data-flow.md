# 상태 관리와 데이터 흐름

## 공통 패턴

- 각 화면은 대응하는 ViewModel을 가짐
- ViewModel은 `StateFlow`로 UI 상태를 노출
- 화면은 `collectAsStateWithLifecycle()`로 상태를 구독
- 네트워크 호출은 대부분 ViewModel 내부에서 직접 수행

## ViewModel별 책임

| ViewModel | 상태 | 주요 동작 |
| --- | --- | --- |
| `HomeViewModel` | 상품 목록, 로딩 | 상품 목록 조회 |
| `SearchViewModel` | 검색어, 결과, 로딩 | 디바운스 검색, 수동 검색 |
| `ProductDetailViewModel` | 상품, 수량, 로딩, 토스트 | 상세 조회, 장바구니 추가 |
| `CartViewModel` | 장바구니 목록, 총액, 로딩 | 조회, 수량 수정, 삭제 |
| `CheckoutViewModel` | 주문 아이템, 로딩, 제출 상태, 토스트 | 주문 데이터 초기화, 주문 생성 |
| `GroupListViewModel` | 공동구매 목록, 필터, 로딩 | 목록 조회, 마감 임박 필터 |
| `GroupDetailViewModel` | 공동구매 상세, 참여 상태, 토스트 | 상세 조회, 참여 |
| `GroupCreateViewModel` | 제출 상태, 토스트 | 모집 생성 |
| `MyPageViewModel` | 프로필, 주문 목록, 참여 목록, 로딩 | 마이페이지 전체 조회 |
| `ProfileEditViewModel` | 프로필, 저장 상태, 토스트 | 프로필 조회, 수정 저장 |

## 데이터 전달 방식

### 인증

- 저장소: `AuthStorage`
- 저장 위치: `SharedPreferences`
- 키: `nutrishare_access_token`

### 화면 인자

- 상품 상세: 라우트 파라미터 `id`
- 공동구매 상세: 라우트 파라미터 `id`
- 주문 완료: 라우트 파라미터 `id`
- 결제 화면: 쿼리 파라미터 `productId`, `quantity`

### 장바구니 -> 결제

- `CartScreen`이 `savedStateHandle["checkoutItems"]`에 리스트 저장
- `CheckoutScreen`이 현재 백스택에서 읽음
- `CheckoutViewModel`도 `SavedStateHandle`을 구독

## 네트워크 처리 방식

- 모든 ViewModel이 `RetrofitClient.instance` 직접 사용
- 실패 처리는 대체로 단순 무시 또는 토스트 메시지 표시
- 로딩 상태는 `MutableStateFlow<Boolean>`로 개별 관리

## 현재 코드 기준 포인트

- ViewModel이 Repository 없이 API를 직접 호출
- 예외 처리 메시지 수준이 화면마다 다름
- 장바구니 결제 전달은 라우트 인자 대신 `savedStateHandle` 의존
