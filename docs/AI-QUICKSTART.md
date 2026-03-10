# AI Quickstart

## 목적

- 이 문서는 다른 AI 모델이 프로젝트를 빠르게 파악하도록 압축한 인덱스다.
- 설명보다 식별, 연결, 책임 분리에 초점을 둔다.

## Entry Points

- App entry: `app/src/main/java/com/example/nutrishare_android/MainActivity.kt`
- Navigation root: `app/src/main/java/com/example/nutrishare_android/navigation/NavGraph.kt`
- Route definitions: `app/src/main/java/com/example/nutrishare_android/navigation/Screen.kt`
- Network root: `app/src/main/java/com/example/nutrishare_android/data/network/RetrofitClient.kt`
- API interface: `app/src/main/java/com/example/nutrishare_android/data/network/ApiService.kt`
- Repository interface: `app/src/main/java/com/example/nutrishare_android/data/repository/NutriRepository.kt`
- Token storage: `app/src/main/java/com/example/nutrishare_android/data/local/AuthStorage.kt`

## Directory Map

- `navigation/`: route constants + NavHost wiring
- `data/model/`: request/response DTOs
- `data/network/`: Retrofit client, auth interceptor, API surface
- `data/repository/`: repository interface + Retrofit-backed implementation
- `data/local/`: token persistence
- `ui/screen/`: Compose screens
- `ui/viewmodel/`: screen state + API calls
- `ui/components/`: shared UI blocks
- `ui/theme/`: theme tokens

## Startup Flow

1. `MainActivity.onCreate()`
2. `RetrofitClient.init(applicationContext)`
3. Compose theme applied
4. `rememberNavController()`
5. `NavGraph(navController, context)`
6. `AuthStorage.isAuthenticated()`
7. Start destination = `home` or `login`

## Route Table

| Route | Screen | Notes |
| --- | --- | --- |
| `login` | `LoginScreen` | stores token after `devLogin()` |
| `home` | `HomeScreen` | product list |
| `search` | `SearchScreen` | debounced product search |
| `groups` | `GroupListScreen` | group-buy list |
| `groups/new` | `GroupCreateScreen` | create group buy |
| `groups/{id}` | `GroupDetailScreen` | load + join group buy |
| `products/{id}` | `ProductDetailScreen` | load product, add to cart, buy now |
| `cart` | `CartScreen` | edit/remove cart items |
| `checkout?...` | `CheckoutScreen` | supports single-product and cart checkout |
| `orders/{id}/complete` | `OrderCompleteScreen` | post-order confirmation |
| `mypage` | `MyPageScreen` | profile + orders + participations |
| `mypage/edit` | `ProfileEditScreen` | edit profile |

## Screen -> ViewModel Mapping

- `HomeScreen` -> `HomeViewModel`
- `SearchScreen` -> `SearchViewModel`
- `ProductDetailScreen` -> `ProductDetailViewModel`
- `CartScreen` -> `CartViewModel`
- `CheckoutScreen` -> `CheckoutViewModel`
- `GroupListScreen` -> `GroupListViewModel`
- `GroupDetailScreen` -> `GroupDetailViewModel`
- `GroupCreateScreen` -> `GroupCreateViewModel`
- `MyPageScreen` -> `MyPageViewModel`
- `ProfileEditScreen` -> `ProfileEditViewModel`
- `LoginScreen`, `OrderCompleteScreen` -> no dedicated ViewModel

## Core Domains

- Auth
- Product
- Cart
- Order
- Group purchase
- My page

## Data Flow Rules

- UI state is held in `MutableStateFlow`
- screens observe via `collectAsStateWithLifecycle()`
- ViewModels depend on `NutriRepository` (default impl uses `RetrofitClient.instance`)
- auth token stored in `SharedPreferences`
- cart-to-checkout payload passed via `savedStateHandle["checkoutItems"]`

## API Summary

### Auth

- `GET auth/dev-login`
- `POST auth/reissue`

### Product

- `GET products`
- `GET products/search`
- `GET products/{id}`

### Cart

- `GET cart`
- `POST cart`
- `PUT cart/{productId}`
- `DELETE cart/{productId}`

### Order

- `POST orders`

### Group Purchase

- `GET groups`
- `GET groups/{id}`
- `POST groups`
- `POST groups/{id}/join`

### User

- `GET users/me`
- `PUT users/me`
- `GET users/me/orders`
- `GET users/me/participations`

## Important Implementation Notes

- Base URL is hardcoded to `http://10.0.2.2:8080/api/v1/`
- Cleartext traffic is enabled in manifest
- `401` triggers token refresh inside OkHttp interceptor
- `LoginScreen` uses `devLogin()` even though UI suggests social login
- `GroupCreateViewModel.productOptions` is static mock-like data
- several error branches silently ignore exceptions

## Shared Components

- Layout: `AppScaffold`, `AppHeader`, `BottomNavBar`
- Lists/cards: `ProductCard`, `GroupBuyingCard`
- Input: `AddressForm`, `QuantitySelector`
- Feedback: `LoadingScreen`, `EmptyState`, `StatusBadge`, `NutriProgressBar`

## Recommended Read Order

1. `MainActivity.kt`
2. `NavGraph.kt`
3. `Screen.kt`
4. `RetrofitClient.kt`
5. `ApiService.kt`
6. `data/repository/`
7. `ui/viewmodel/`
8. `ui/screen/`
9. `ui/components/`
