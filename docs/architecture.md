# 구조 개요

## 최상위 구조

- `app/src/main/java/com/example/nutrishare_android/MainActivity.kt`
  - 앱 진입점
  - `RetrofitClient` 초기화
  - Compose 테마 적용 후 `NavGraph` 실행
- `navigation/`
  - `Screen.kt`: 라우트 정의
  - `NavGraph.kt`: 시작 화면과 전체 화면 연결
- `data/`
  - `network/`: Retrofit 설정과 API 인터페이스
  - `model/`: 서버 응답/요청 모델
  - `local/`: 토큰 저장
- `ui/`
  - `screen/`: 실제 화면
  - `viewmodel/`: 화면 상태와 API 호출
  - `components/`: 공용 UI 조각
  - `theme/`: 색상, 타이포, 테마

## 계층 역할

- `screen`
  - Compose UI
  - 사용자 입력 처리
  - `collectAsStateWithLifecycle()`로 상태 구독
  - 화면 이동 처리
- `viewmodel`
  - `MutableStateFlow` 기반 상태 보관
  - `viewModelScope.launch`로 비동기 처리
  - `RetrofitClient.instance`를 직접 호출
- `data`
  - 서버 요청/응답 모델 정의
  - 인증 토큰 저장
  - 공통 네트워크 클라이언트 제공

## 시작 흐름

1. `MainActivity`에서 `RetrofitClient.init(applicationContext)` 호출
2. `NavGraph`에서 `AuthStorage(context)` 생성
3. 토큰 유무에 따라 시작 화면 결정
4. 각 화면이 필요한 ViewModel을 생성하고 API 호출

## 현재 구조 특징

- 모듈은 `:app` 하나
- Repository 계층 없이 ViewModel이 API를 직접 호출
- 인증 상태는 `SharedPreferences` 기반
- 화면 간 일부 데이터 전달은 `savedStateHandle` 사용
