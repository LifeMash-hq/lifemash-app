# Edge-to-Edge & 시스템 인셋 정책

## 원칙

**화면은 자기 콘텐츠만 신경 쓴다. 시스템 인셋은 컨테이너가 결정한다.**

## 설정

```kotlin
// MainActivity
enableEdgeToEdge()  // 한 번만 호출
```

이후 모든 인셋 처리는 Compose 레이어에서 담당한다.

## 화면 유형별 인셋 처리

### 1. 탭 화면 (BottomBar 있음)

Feed, Profile, Notification 등 `MainTabScreen` 내부 화면.

| 인셋 | 처리 위치 |
|------|----------|
| `navigationBars` | `BottomTabBar` / `AdaptiveNavigation`이 소비 (이미 적용됨) |
| `statusBars` | 각 Screen의 루트 `Modifier`에 적용 |

```kotlin
// FeedScreen.kt
Column(modifier.fillMaxSize().statusBarsPadding()) { ... }
```

### 2. 전체화면 (BottomBar 없음)

Auth, EventDetail, EventCreate, Onboarding, ProfileEdit 등 NavHost의 독립 route.

| 인셋 | 처리 위치 |
|------|----------|
| `statusBars` | Screen 루트 Modifier |
| `navigationBars` | Screen 루트 Modifier |

```kotlin
// AuthScreen.kt
Column(modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) { ... }
```

### 3. BottomSheet

BottomSheet는 시스템 네비게이션 바 위에 올라오므로 **Sheet 내부에서 직접 처리**.

```kotlin
Spacer(modifier = Modifier.navigationBarsPadding())
```

### 4. 키보드 (IME)

텍스트 입력이 있는 화면만 해당 Composable에서 `imePadding()` 적용.

```kotlin
Column(modifier.fillMaxSize().imePadding()) { ... }
```

## 금지 사항

- MainScreen / NavHost 레벨에서 일괄 `systemBarsPadding()` 적용 금지
  - 탭 화면과 전체화면의 인셋 정책이 다르기 때문
- `WindowInsets.systemBars.asPaddingValues()`로 수동 계산 금지
  - Compose의 `statusBarsPadding()` / `navigationBarsPadding()` API 사용

## 인셋 소비 흐름

```
Activity (enableEdgeToEdge)
└─ MainScreen (인셋 정책 없음)
   ├─ MainTabScreen
   │  ├─ BottomTabBar → navigationBars 소비
   │  └─ 탭 콘텐츠 → statusBars만 소비
   ├─ Auth (전체화면) → statusBars + navigationBars 소비
   ├─ EventDetail (전체화면) → statusBars + navigationBars 소비
   └─ Onboarding (전체화면) → statusBars + navigationBars 소비
```
