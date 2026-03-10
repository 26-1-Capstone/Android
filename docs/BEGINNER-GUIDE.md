# 초보자용 읽기 가이드

## 이 문서 보는 법

- 이 문서는 앱이 어디서 시작해서 어디로 흘러가는지 한 줄씩 따라가기 위한 문서다.
- 코드를 처음 읽는 사람 기준으로, 먼저 봐야 할 파일과 이유를 같이 적었다.
- 순서대로 읽으면 된다.

## 1. 앱은 어디서 시작하나

- 먼저 `app/src/main/java/com/example/nutrishare_android/MainActivity.kt`를 연다.
- 안드로이드 앱은 보통 `Activity`에서 시작한다.
- 이 프로젝트에서는 `MainActivity`가 시작점이다.
- `onCreate()`가 처음 실행된다.
- 여기서 `RetrofitClient.init(applicationContext)`를 호출한다.
- 이 줄은 네트워크 코드가 앱의 `Context`를 쓸 수 있게 준비하는 단계다.
- 그 다음 `setContent { ... }`가 나온다.
- 이 프로젝트 화면은 XML이 아니라 Compose 코드로 그린다.
- `rememberNavController()`는 화면 이동을 담당하는 객체를 만든다.
- 마지막에 `NavGraph(...)`를 호출한다.
- 그래서 실제 화면 흐름은 `NavGraph.kt`로 넘어가서 읽으면 된다.

## 2. 첫 화면은 어떻게 정하나

- 다음으로 `app/src/main/java/com/example/nutrishare_android/navigation/NavGraph.kt`를 연다.
- `NavGraph`는 화면 목록과 화면 이동 규칙을 모아둔 곳이다.
- 여기서 `AuthStorage(context)`를 만든다.
- `AuthStorage`는 로그인 토큰을 저장하고 꺼내는 클래스다.
- `authStorage.isAuthenticated()`가 `true`면 홈으로 시작한다.
- `false`면 로그인 화면으로 시작한다.
- 즉, 첫 화면 결정 기준은 토큰 존재 여부다.

## 3. 화면 이름은 어디에 모여 있나

- `app/src/main/java/com/example/nutrishare_android/navigation/Screen.kt`를 본다.
- 이 파일은 화면 주소를 한곳에 모아둔 파일이다.
- 예를 들어 `home`, `login`, `cart` 같은 값이 여기에 있다.
- `products/{id}`처럼 뒤에 숫자를 붙여서 쓰는 화면도 여기에 있다.
- `createRoute(id)` 같은 함수는 실제 주소 문자열을 만들어 준다.
- 화면 이동 코드를 읽다가 문자열이 나오면 다시 이 파일에서 뜻을 확인하면 된다.

## 4. 로그인 정보는 어디에 저장하나

- `app/src/main/java/com/example/nutrishare_android/data/local/AuthStorage.kt`를 본다.
- 이 클래스는 `SharedPreferences`를 사용한다.
- 쉽게 말해 앱 안에 작은 설정 저장소를 쓰는 방식이다.
- 토큰 키 이름은 `nutrishare_access_token`이다.
- `getToken()`은 토큰을 읽는다.
- `setToken()`은 토큰을 저장한다.
- `removeToken()`은 로그아웃할 때 토큰을 지운다.
- `isAuthenticated()`는 토큰이 비어 있지 않은지 확인한다.

## 5. 서버 연결은 어디서 하나

- `app/src/main/java/com/example/nutrishare_android/data/network/RetrofitClient.kt`를 연다.
- 이 파일이 서버 통신의 중심이다.
- `BASE_URL`에 서버 주소가 들어 있다.
- 지금은 `http://10.0.2.2:8080/api/v1/`로 고정돼 있다.
- `10.0.2.2`는 안드로이드 에뮬레이터에서 PC 로컬 서버를 가리킬 때 자주 쓴다.
- `authInterceptor`는 모든 요청에 토큰을 붙이는 역할을 한다.
- 토큰이 있으면 `Authorization: Bearer {token}` 헤더를 추가한다.
- 서버가 `401`을 주면 토큰 재발급도 시도한다.
- 마지막의 `instance`가 실제 API 호출에 쓰이는 객체다.

## 6. 서버 API 목록은 어디 있나

- `app/src/main/java/com/example/nutrishare_android/data/network/ApiService.kt`를 본다.
- 이 파일은 서버에 어떤 요청을 보낼 수 있는지 적은 목록이다.
- `@GET`, `@POST`, `@PUT`, `@DELETE`는 HTTP 메서드다.
- 예를 들어 `getProducts()`는 상품 목록을 가져온다.
- `getProductDetail(id)`는 특정 상품 하나를 가져온다.
- `getCart()`는 장바구니를 가져온다.
- `createOrder()`는 주문을 만든다.
- `getMyProfile()`은 내 프로필을 가져온다.
- 화면이 어떤 API를 쓰는지 알고 싶으면 ViewModel과 이 파일을 같이 보면 된다.

## 7. 서버 응답 데이터는 어디에 담기나

- `app/src/main/java/com/example/nutrishare_android/data/model/Models.kt`를 연다.
- 이 파일에는 서버와 주고받는 데이터 모양이 정의돼 있다.
- `Product`는 상품 데이터다.
- `Group`은 공동구매 데이터다.
- `CartItem`은 장바구니 상품 한 줄이다.
- `Order`는 주문 정보다.
- `User`는 사용자 정보다.
- `ApiResponse<T>`는 대부분의 응답을 감싸는 공통 껍데기다.
- 코드에서 `response.body()?.data`를 많이 쓰는 이유가 여기 있다.

## 8. 화면 코드는 어디서 읽나

- `app/src/main/java/com/example/nutrishare_android/ui/screen/` 폴더를 본다.
- 파일 하나가 보통 화면 하나다.
- 화면 파일은 버튼, 텍스트, 리스트 같은 UI를 그린다.
- 화면 파일은 상태를 직접 오래 들고 있기보다 ViewModel에서 가져온다.
- 그래서 화면을 읽을 때는 대응하는 ViewModel도 같이 보는 게 좋다.

## 9. ViewModel은 왜 필요한가

- `app/src/main/java/com/example/nutrishare_android/ui/viewmodel/` 폴더를 본다.
- ViewModel은 화면에 필요한 데이터를 관리한다.
- 서버 호출도 주로 여기서 한다.
- 화면은 ViewModel이 준 상태를 그리는 역할에 가깝다.
- 이 프로젝트는 Repository 없이 ViewModel이 API를 직접 호출한다.
- 그래서 흐름이 단순해서 초보자가 따라가기 쉽다.

## 10. 홈 화면은 어떻게 동작하나

- `HomeScreen.kt`와 `HomeViewModel.kt`를 같이 연다.
- `HomeViewModel`은 시작하자마자 `fetchProducts()`를 호출한다.
- 여기서 `ApiService.getProducts()`를 호출한다.
- 받아온 결과를 `_products`에 넣는다.
- `HomeScreen`은 `products` 상태를 구독해서 리스트를 그린다.
- 상품 카드를 누르면 상품 상세 화면으로 이동한다.

## 11. 검색 화면은 어떻게 동작하나

- `SearchScreen.kt`와 `SearchViewModel.kt`를 본다.
- 검색어는 `_query`에 저장된다.
- 사용자가 입력하면 `debounce(300)`으로 잠깐 기다린다.
- 바로 서버를 계속 호출하지 않도록 하는 장치다.
- 글자가 비어 있지 않으면 `doSearch(q)`를 호출한다.
- 결과는 `_results`에 저장된다.
- 화면은 그 결과를 보여 준다.

## 12. 상품 상세에서 장바구니와 바로구매는 어떻게 다르나

- `ProductDetailScreen.kt`와 `ProductDetailViewModel.kt`를 본다.
- `loadProduct(id)`가 상세 정보를 가져온다.
- 수량은 `_quantity`에 저장된다.
- 장바구니 담기는 `addToCart()`를 호출한다.
- 바로구매는 장바구니를 거치지 않고 `checkout` 화면으로 바로 이동한다.
- 이때 상품 ID와 수량을 라우트 인자로 넘긴다.

## 13. 장바구니 화면은 무엇을 하나

- `CartScreen.kt`와 `CartViewModel.kt`를 본다.
- 시작하면 `fetchCart()`로 장바구니를 불러온다.
- 수량 변경은 `updateCartItem()` API를 쓴다.
- 삭제는 `removeCartItem()` API를 쓴다.
- 주문 버튼을 누르면 결제 화면으로 이동한다.
- 이때 장바구니 상품 목록을 `savedStateHandle`에 넣는다.
- 즉, 장바구니에서 결제 화면으로 데이터를 넘기는 특별 통로를 쓰고 있다.

## 14. 결제 화면은 왜 조금 복잡한가

- `CheckoutScreen.kt`와 `CheckoutViewModel.kt`를 본다.
- 결제 화면은 두 가지 경우를 처리한다.
- 첫 번째는 상품 상세에서 바로 구매로 들어온 경우다.
- 두 번째는 장바구니에서 여러 상품을 들고 들어온 경우다.
- 장바구니에서 왔으면 `savedStateHandle["checkoutItems"]`를 읽는다.
- 바로구매면 `productId`와 `quantity`로 상품 상세를 다시 조회한다.
- 배송지는 `AddressForm`으로 입력한다.
- 주문 버튼을 누르면 `createOrder()` API를 호출한다.
- 주문이 성공하면 주문 완료 화면으로 이동한다.

## 15. 공동구매 화면은 무엇을 보나

- `GroupListScreen.kt`, `GroupDetailScreen.kt`, `GroupCreateScreen.kt`를 본다.
- 목록 화면은 `getGroups()`로 전체를 불러온다.
- `CLOSING_SOON` 필터는 서버가 아니라 앱 안에서 날짜 계산으로 거른다.
- 상세 화면은 `getGroupDetail(id)`로 하나를 가져온다.
- 참여 버튼은 `joinGroup(id)`를 호출한다.
- 생성 화면은 `createGroup()`을 호출한다.
- 생성 화면의 상품 목록은 지금 서버에서 받지 않고 ViewModel 안에 고정값으로 들어 있다.

## 16. 마이페이지는 무엇을 동시에 읽나

- `MyPageScreen.kt`와 `MyPageViewModel.kt`를 본다.
- 여기서는 프로필, 주문 목록, 참여 목록을 한 번에 불러온다.
- `fetchAll()` 안에서 세 API를 각각 `launch`로 동시에 호출한다.
- 그래서 한 화면에서 여러 정보를 같이 보여 줄 수 있다.
- `ProfileEditScreen.kt`와 `ProfileEditViewModel.kt`는 수정 전용 화면이다.
- 저장 버튼을 누르면 `updateMyProfile()`을 호출한다.

## 17. 공용 UI는 어디서 재사용하나

- `ui/components/` 폴더를 본다.
- `AppScaffold`는 화면의 공통 뼈대다.
- `AppHeader`는 상단 헤더다.
- `BottomNavBar`는 하단 탭바다.
- `ProductCard`는 상품 카드다.
- `GroupBuyingCard`는 공동구매 카드다.
- `QuantitySelector`는 수량 조절 UI다.
- `AddressForm`은 주소 입력 폼이다.
- 여러 화면에서 공통으로 쓰는 조각은 여기서 찾으면 된다.

## 18. 초보자가 실제로 코드를 읽는 추천 순서

1. `MainActivity.kt`
2. `NavGraph.kt`
3. `Screen.kt`
4. `AuthStorage.kt`
5. `RetrofitClient.kt`
6. `ApiService.kt`
7. `Models.kt`
8. `HomeViewModel.kt` -> `HomeScreen.kt`
9. `ProductDetailViewModel.kt` -> `ProductDetailScreen.kt`
10. `CartViewModel.kt` -> `CartScreen.kt`
11. `CheckoutViewModel.kt` -> `CheckoutScreen.kt`
12. `MyPageViewModel.kt` -> `MyPageScreen.kt`
13. `ui/components/`

## 19. 읽다가 막히면 먼저 확인할 질문

- 이 화면은 어떤 ViewModel을 쓰나
- 이 ViewModel은 어떤 API를 호출하나
- 응답 데이터는 `Models.kt`의 어떤 클래스에 담기나
- 다음 화면으로 이동할 때 무엇을 인자로 넘기나
- 토큰이 필요한 요청인가

## 20. 이 프로젝트를 한 문장으로 이해하면

- 화면이 ViewModel의 `StateFlow`를 구독하고, ViewModel이 Retrofit으로 서버를 호출해 받은 데이터를 다시 화면에 뿌려주는 구조다.
