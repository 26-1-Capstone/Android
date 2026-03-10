# 프로젝트 문서

빠르게 훑을 때는 아래 순서로 보면 됩니다.

1. [AI Quickstart](./AI-QUICKSTART.md)
2. [초보자용 읽기 가이드](./BEGINNER-GUIDE.md)
3. [구조 개요](./architecture.md)
4. [화면과 사용자 흐름](./screens.md)
5. [상태 관리와 데이터 흐름](./data-flow.md)
6. [API와 모델](./api.md)
7. [공용 UI 컴포넌트](./components.md)
8. [설정과 확인 포인트](./setup-notes.md)

## 바로 보기

- 앱 시작점: `MainActivity` -> `RetrofitClient.init()` -> `NavGraph`
- 시작 화면 결정: `AuthStorage.isAuthenticated()` 기준
- 주요 도메인: 상품, 장바구니, 주문, 공동구매, 마이페이지
- 공통 레이아웃: `AppScaffold`
- 네트워크 진입점: `RetrofitClient.instance`

## 용도별 추천

- 다른 AI 모델에 바로 넣을 요약: `AI-QUICKSTART.md`
- 처음 코드 읽는 사람용 설명: `BEGINNER-GUIDE.md`
- 화면별 책임과 구조 확인: 나머지 세부 문서
