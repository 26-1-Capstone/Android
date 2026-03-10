# 설정과 확인 포인트

## 빌드/실행 관련

- `compileSdk = 36`
- `minSdk = 34`
- `targetSdk = 36`
- Java/Kotlin 타깃은 `11`
- Compose 사용

## 의존성

- Material3
- Navigation Compose
- Lifecycle ViewModel Compose
- Retrofit2 + Gson
- OkHttp Logging Interceptor
- Coil Compose

## 매니페스트

- `INTERNET` 권한 포함
- `usesCleartextTraffic = true`

## 코드에서 바로 보이는 운영 포인트

- 서버 주소가 `RetrofitClient`에 하드코딩되어 있음
- 로그인 화면의 버튼 문구와 실제 로그인 로직은 `devLogin()` 기준
- 공동구매 생성용 상품 선택 목록은 `GroupCreateViewModel.productOptions`에 고정값으로 들어 있음
- `401` 재발급 로직은 `RetrofitClient` 인터셉터 내부에서 처리

## 문서 갱신이 필요한 시점

- 라우트가 추가되거나 이름이 바뀔 때
- API 엔드포인트나 요청/응답 모델이 바뀔 때
- 공용 컴포넌트 책임이 달라질 때
- 인증 방식이나 서버 주소 관리 방식이 바뀔 때
