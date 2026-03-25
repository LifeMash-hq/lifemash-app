# LifeMash

KMP(Kotlin Multiplatform) 기반 모바일 앱 + Ktor 백엔드를 하나의 모노레포로 관리하는 프로젝트입니다.

## Architecture

### Module Structure

```mermaid
flowchart LR
    subgraph SHARED["Shared Layer"]
        MODEL["shared:model\nKMP DTO"]
    end

    subgraph BACKEND["Backend Layer"]
        API["backend:api\nInterfaces"]
        STUB["backend:stub\nDemo"]
        SERVER["backend:server\nKtor"]
    end

    subgraph PRIVATE["Private"]
        CORE["lifemash-core\nImplementations"]
    end

    subgraph CLIENT["App Layer"]
        DATA["feature:*/data"]
        DOMAIN["feature:*/domain"]
        UI["feature:*/ui"]
        MAIN["feature:main"]
        TARGETS{{"Android / iOS"}}
    end

    MODEL --> API
    MODEL --> DATA

    API --> STUB
    API --> CORE
    API --> SERVER

    STUB -.->|"fallback"| SERVER
    CORE -.->|"production"| SERVER

    DATA --> DOMAIN --> UI --> MAIN --> TARGETS

    style MODEL fill:#4CAF50,color:#fff,stroke:#388E3C
    style API fill:#2196F3,color:#fff,stroke:#1565C0
    style STUB fill:#FF9800,color:#fff,stroke:#E65100
    style SERVER fill:#9C27B0,color:#fff,stroke:#6A1B9A
    style CORE fill:#F44336,color:#fff,stroke:#C62828
    style DATA fill:#455A64,color:#fff,stroke:#263238
    style DOMAIN fill:#546E7A,color:#fff,stroke:#37474F
    style UI fill:#607D8B,color:#fff,stroke:#455A64
    style MAIN fill:#78909C,color:#fff,stroke:#546E7A
    style TARGETS fill:#00BCD4,color:#fff,stroke:#00838F
```

### Request Flow

```mermaid
sequenceDiagram
    participant App as Android / iOS
    participant Data as feature:*/data
    participant Net as shared:network
    participant Route as backend:server
    participant Svc as AuthService (Interface)
    participant Impl as lifemash-core or Stub

    App->>Data: 카카오 로그인
    Data->>Net: POST /api/v1/auth/kakao
    Net->>Route: HTTP Request
    Route->>Svc: inject<AuthService>()
    Svc->>Impl: signInWithKakao()
    Impl-->>Svc: AuthTokenDto
    Svc-->>Route: Response
    Route-->>Net: JSON
    Net-->>Data: AuthTokenDto
    Data-->>App: AuthToken (Domain)
```

### Dependency Direction

```mermaid
flowchart LR
    A["shared:model"] --> B["backend:api"]
    B --> C["lifemash-core"]
    C --> D["backend:server"]
    B --> E["backend:stub"]
    E --> D

    style A fill:#4CAF50,color:#fff,stroke:#388E3C
    style B fill:#2196F3,color:#fff,stroke:#1565C0
    style C fill:#F44336,color:#fff,stroke:#C62828
    style D fill:#9C27B0,color:#fff,stroke:#6A1B9A
    style E fill:#FF9800,color:#fff,stroke:#E65100
```

## Modules

| Module | Type | Description |
|--------|------|-------------|
| `shared:model` | KMP | 앱 ↔ 백엔드 공통 DTO |
| `shared:network` | KMP | Ktor HttpClient (OkHttp / Darwin) |
| `shared:designsystem` | KMP | Material3 디자인 토큰, 공통 Composable |
| `backend:api` | JVM | 서비스/레포지토리/클라이언트 인터페이스 |
| `backend:stub` | JVM | 인터페이스 데모 구현체 (공개 빌드용) |
| `backend:server` | JVM | Ktor Routes, Plugins, Koin DI |
| `feature:auth` | KMP | 카카오/구글 소셜 로그인 |
| `feature:calendar` | KMP | 캘린더 이벤트/그룹/댓글 |
| `feature:assistant` | KMP | AI 어시스턴트 (Claude API) |
| `feature:home` | KMP | 홈 블록 + 마켓플레이스 |
| `feature:notification` | KMP | 키워드 알림 + FCM |

## Tech Stack

- **Kotlin Multiplatform** — Android + iOS 단일 코드베이스
- **Compose Multiplatform** — 선언적 UI
- **Ktor** — 클라이언트 (앱) + 서버 (백엔드)
- **Exposed** — Kotlin SQL ORM
- **Koin** — 멀티플랫폼 DI
- **PostgreSQL** — Neon (Serverless)
- **Firebase** — Analytics, Crashlytics, FCM
- **GitHub Packages** — 모듈 간 아티팩트 배포

## Build

```bash
# 전체 백엔드 빌드
./gradlew :backend:server:build

# Android 앱 빌드
./gradlew :app:assembleDebug

# 백엔드 로컬 실행
./gradlew :backend:server:run
```

## License

[MIT](LICENSE)
