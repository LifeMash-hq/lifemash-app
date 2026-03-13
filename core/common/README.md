# core:common

여러 feature 모듈이 공통으로 사용하는 **순수 Kotlin 유틸리티** 모듈.

## 이 모듈에 들어올 수 있는 것

- 날짜/시간 포맷터 등 표시(display) 관련 유틸
- 여러 feature에서 반복되는 확장함수
- Android 의존성이 없는 공통 헬퍼

## 이 모듈에 들어오면 안 되는 것

- 도메인 모델 (→ `domain:core`)
- UI 컴포넌트, Compose 코드 (→ `core:designsystem`)
- 네트워크/DB 관련 코드 (→ `core:network`, `data:*`)
- 특정 feature에서만 쓰이는 코드 (→ 해당 feature 모듈)
- Android 프레임워크 의존성이 필요한 코드

## 의존성 규칙

이 모듈은 다른 프로젝트 모듈에 의존하지 않는다.
`core:common`에 의존하는 모듈이 많아질수록 변경 비용이 커지므로,
코드를 추가하기 전에 정말 여러 모듈에서 쓰이는지 확인할 것.
