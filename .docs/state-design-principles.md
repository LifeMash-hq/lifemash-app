# Android 상태 설계 원칙

> Compose + ViewModel 기반 Android/KMP 프로젝트에서 UiState를 설계할 때  
> sealed interface의 오용이 만드는 구조적 복잡성과, 이를 피하기 위한 원칙을 정리한다.

---

## 목차

1. [문제 제기 — sealed interface는 왜 매력적인가](#1-문제-제기--sealed-interface는-왜-매력적인가)
2. [관찰 — sealed interface가 유발하는 부차적 복잡성](#2-관찰--sealed-interface가-유발하는-부차적-복잡성)
3. [분석 — 두 가지 관심사의 혼동](#3-분석--두-가지-관심사의-혼동)
4. [원칙 — UiState 타입 분리의 기준](#4-원칙--uistate-타입-분리의-기준)
5. [설계 — 단일 data class 기반 상태 모델](#5-설계--단일-data-class-기반-상태-모델)
6. [동시성 — 상태 업데이트와 경쟁 조건](#6-동시성--상태-업데이트와-경쟁-조건)
7. [상태 설계 원칙 — 변하지 않는 것들](#7-상태-설계-원칙--변하지-않는-것들)
8. [결론](#8-결론)

---

## 1. 문제 제기 — sealed interface는 왜 매력적인가

MVVM 아키텍처에서 화면 상태를 다음과 같이 설계하는 것은 널리 알려진 관행이다:

```kotlin
sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Loaded(
        val profile: UserProfile,
        val isFollowInProgress: Boolean = false,
        // ...
    ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}
```

이 설계의 매력은 명확하다:

- **상호 배타성 보장**: Loading이면서 동시에 Loaded일 수 없다.
- **when 분기 강제**: 새로운 상태를 추가하면 컴파일러가 모든 처리 지점을 알려준다.
- **의도 전달**: 화면이 가질 수 있는 상태가 타입 수준에서 열거된다.

이러한 장점 때문에 많은 프로젝트에서 UiState를 sealed interface로 설계한다. 그러나 이 설계가 프로젝트 전체에 미치는 영향은 충분히 논의되지 않는다.

---

## 2. 관찰 — sealed interface가 유발하는 부차적 복잡성

sealed interface로 UiState를 나누면, ViewModel의 거의 모든 함수에서 다음 패턴이 반복된다:

```kotlin
fun toggleFollow(userId: String) {
    val state = _uiState.value as? ProfileUiState.Loaded ?: return
    // ...
}

fun selectTab(tab: Tab) {
    _uiState.update { state ->
        if (state is ProfileUiState.Loaded) state.copy(selectedTab = tab) else state
    }
}
```

이 반복은 단순한 보일러플레이트가 아니다. 개발자는 이 반복을 줄이기 위해 점점 더 정교한 패턴을 도입하게 된다:

### 2.1 Loaded-param 패턴

"함수가 Loaded 상태에서만 호출 가능하다"는 것을 파라미터 타입으로 표현하려는 시도:

```kotlin
fun toggleFollow(state: ProfileUiState.Loaded, userId: String) {
    // 캐스팅 불필요 — 이미 Loaded가 보장됨
}
```

그러나 이 패턴은 새로운 문제를 만든다. 파라미터로 받은 `state`는 호출 시점의 스냅샷이므로, 함수 실행 시점의 현재 상태와 다를 수 있다. "지금 이 작업을 실행해도 되는가"를 판단하려면 결국 `_uiState.value`를 읽어야 한다.

### 2.2 멤버 확장 함수 패턴

Loaded-param 패턴의 호출부를 자연스럽게 만들기 위한 시도:

```kotlin
// ViewModel 내부
fun ProfileUiState.Loaded.toggleFollow(userId: String) {
    val current = _uiState.value as? ProfileUiState.Loaded ?: return
    // ...
}

// 호출부
with(viewModel) {
    (uiState as? ProfileUiState.Loaded)?.toggleFollow(userId)
}
```

`this`는 "이 함수는 Loaded 컨텍스트에서만 호출 가능하다"는 타입 게이트 역할을 하고, 실제 로직은 `_uiState.value`를 사용한다. 타입 안전성과 현재 상태 확인을 모두 잡으려는 절충안이지만, 패턴 자체의 복잡성이 높다.

### 2.3 updateLoaded 헬퍼

`_uiState.update` 안에서 매번 캐스팅하는 것이 어색하여 도입하는 헬퍼:

```kotlin
private inline fun updateLoaded(block: ProfileUiState.Loaded.() -> ProfileUiState.Loaded) {
    _uiState.update { (it as? ProfileUiState.Loaded)?.block() ?: it }
}
```

### 2.4 복잡성의 연쇄

| 도입한 패턴 | 도입 이유 |
|------------|----------|
| Loaded-param | 캐스팅을 호출부에 강제하려고 |
| 멤버 확장 함수 | Loaded-param의 호출부를 자연스럽게 하려고 |
| updateLoaded 헬퍼 | update 안에서 캐스팅이 어색해서 |
| 가드절 반복 | Loaded인지 매번 확인해야 해서 |

이 모든 패턴은 **sealed interface를 사용한 결과로 발생한 부차적 복잡성**이다. 문제를 해결하는 것이 아니라, 문제가 만든 문제를 해결하고 있다.

---

## 3. 분석 — 두 가지 관심사의 혼동

sealed interface 기반 UiState는 두 가지 서로 다른 관심사를 하나의 메커니즘으로 해결하려 한다:

### 관심사 A: 화면 전체의 구조적 전환

> "이 화면은 지금 스켈레톤을 보여줘야 하는가, 콘텐츠를 보여줘야 하는가, 에러 페이지를 보여줘야 하는가?"

이것은 **화면의 레이아웃 자체가 바뀌는** 전환이다. Loading일 때는 버튼도, 리스트도, 탭도 의미가 없다.

### 관심사 B: 개별 작업의 실행 가능성 제어

> "팔로우 버튼을 지금 눌러도 되는가? 저장 중인가?"

이것은 **화면 레이아웃은 유지된 채** 특정 인터랙션의 가능 여부가 달라지는 것이다.

sealed interface는 관심사 A를 위한 도구다. 그러나 관심사 B까지 sealed interface에 인코딩하면, Loaded 안에 세부 상태가 계속 쌓이고, Loaded에서만 의미 있는 함수들이 캐스팅 문제를 일으킨다.

---

## 4. 원칙 — UiState 타입 분리의 기준

### sealed interface를 사용하는 기준

> **화면 전체의 구조(레이아웃)가 완전히 달라지는 전환에만 sealed interface를 사용한다.**

구체적으로:

| 상태 전환 | sealed interface 적합 여부 |
|----------|--------------------------|
| 첫 진입 시 전체 스켈레톤 표시 | 적합 |
| 인증 실패로 로그인 화면 전환 | 적합 |
| 네트워크 에러로 전체 화면을 에러 페이지로 교체 | 적합 |
| 저장 버튼이 로딩 스피너로 변경 | **부적합** |
| 팔로우 요청 중 버튼 비활성화 | **부적합** |
| 바텀시트 표시/숨김 | **부적합** |
| 에러 토스트 표시 | **부적합** |

부적합한 항목들은 **화면 레이아웃이 유지된 채 부분적으로 변하는 상태**다. 이것은 sealed interface가 아니라 `data class`의 필드로 표현해야 한다.

### 왜 이 기준인가

sealed interface의 `when` 분기는 컴포저블에서 **화면 전체를 교체하는 분기점**에 대응한다:

```kotlin
when (uiState) {
    is Loading -> FullScreenSkeleton()
    is Loaded -> ActualContent(uiState)
    is Error -> FullScreenError(uiState.message)
}
```

이 분기가 자연스러운 곳에서만 sealed interface가 정당화된다. `Loaded` 안에서 다시 세부 상태를 분기해야 한다면, 그것은 sealed interface의 범위를 벗어난 것이다.

---

## 5. 설계 — 단일 data class 기반 상태 모델

위 원칙을 적용하면, 대부분의 화면 상태는 다음과 같이 설계된다:

```kotlin
data class ProfileUiState(
    // 초기화 상태
    val isInitializing: Boolean = true,
    val initError: String? = null,

    // 도메인 데이터
    val profile: UserProfile? = null,
    val moments: List<Moment> = emptyList(),

    // 인터랙션 상태
    val isFollowInProgress: Boolean = false,
    val isSaving: Boolean = false,

    // UI 상태
    val selectedTab: ProfileSubTab = ProfileSubTab.Moments,
    val isFollowSheetVisible: Boolean = false,

    // 이벤트
    val event: ProfileEvent? = null,
)
```

이 설계에서 ViewModel 함수는 캐스팅 없이 작성된다:

```kotlin
fun toggleFollow(userId: String) {
    if (uiState.value.isFollowInProgress) return
    _uiState.update { it.copy(isFollowInProgress = true) }
    viewModelScope.launch {
        runCatching {
            // ...
        }
    }
}
```

화면 전체의 구조적 전환이 필요한 경우에만 sealed interface를 사용하되, 이를 UiState 안의 한 필드로 한정할 수 있다:

```kotlin
data class ProfileUiState(
    val screenPhase: ScreenPhase = ScreenPhase.Initializing,
    // ... 나머지 필드
)

sealed interface ScreenPhase {
    data object Initializing : ScreenPhase
    data object Ready : ScreenPhase
    data class FatalError(val message: String) : ScreenPhase
}
```

이렇게 하면 sealed interface의 장점(화면 구조 전환의 명시성)은 유지하면서, 나머지 상태는 캐스팅 없이 접근할 수 있다.

---

## 6. 동시성 — 상태 업데이트와 경쟁 조건

UiState의 타입 설계와 별개로, MutableStateFlow를 사용하는 ViewModel에서는 동시성 문제를 이해해야 한다.

### 6.1 스냅샷 캡처와 현재 상태의 차이

```kotlin
fun toggleFollow(userId: String) {
    val snapshot = _uiState.value       // 이 시점의 스냅샷
    _uiState.update { it.copy(...) }    // 최신 상태 기준으로 적용

    viewModelScope.launch {
        // 여기서 snapshot을 사용하면 이미 과거의 상태
        // 다른 코루틴이 _uiState를 변경했을 수 있음
    }
}
```

### 6.2 원칙: 가드절과 상태 업데이트는 목적이 다르다

| 목적 | 방법 | 시점 |
|------|------|------|
| "이 작업을 실행해도 되는가" | `_uiState.value` 직접 읽기 (가드절) | 함수 진입 시 1회 |
| "상태를 안전하게 변경한다" | `_uiState.update { }` (CAS 루프) | 변경이 필요한 모든 시점 |
| "특정 값을 즉시 반영한다" | `_uiState.value = ...` (직접 대입) | 가드 직후, 코루틴 전환 전 |

### 6.3 코루틴 복귀 후에는 반드시 update를 사용한다

`suspend` 함수 호출이나 디스패처 전환 후에는 다른 코루틴이 상태를 변경했을 수 있다.

```kotlin
fun toggleFollow(userId: String) {
    if (_uiState.value.isFollowInProgress) return  // 가드: 1회

    _uiState.value = _uiState.value.copy(isFollowInProgress = true)  // 즉시 반영

    viewModelScope.launch {
        runCatching {
            followUser(userId)  // suspend — 여기서 컨텍스트 전환 발생
        }.onSuccess {
            // 캡처된 스냅샷이 아닌 update 사용 — 다른 코루틴의 변경을 보존
            _uiState.update { it.copy(isFollowInProgress = false) }
        }.onFailure {
            _uiState.update { it.copy(isFollowInProgress = false, error = "실패") }
        }
    }
}
```

캡처된 스냅샷으로 `_uiState.value = snapshot.copy(...)`를 하면 다른 코루틴이 변경한 필드를 덮어쓴다. `update`는 CAS(Compare-And-Set) 루프로 항상 최신 상태에 변환을 적용하므로 이 문제를 방지한다.

### 6.4 단일 data class에서는 이 원칙이 자연스럽다

sealed interface 기반에서는 `update` 안에서 매번 캐스팅이 필요해 `as`, `as?`, 헬퍼 함수 등이 개입된다. 단일 `data class`에서는 `it.copy(...)`만으로 완결되므로 동시성 원칙과 코드가 충돌하지 않는다.

### 6.5 상태 변경은 public 함수에서만

`_uiState.update` / `_uiState.value =` 호출은 **public 함수에서만** 허용한다. private 함수는 데이터를 **반환**만 하고, 상태 변경은 호출한 public 함수가 담당한다.

**왜 이 규칙인가:**

- 상태 변경 지점이 public 함수로 한정되면, 해당 ViewModel의 상태 흐름을 public 함수만 읽어서 파악할 수 있다
- private 함수가 상태를 직접 변경하면 변경 지점이 흩어져 추적이 어렵고, 의도치 않은 순서로 상태가 바뀔 위험이 있다
- 테스트에서도 public 함수 호출 → 상태 확인의 단순한 구조가 유지된다

```kotlin
// ❌ private 함수가 상태를 직접 변경 — 변경 지점이 흩어짐
private suspend fun loadEvents(gId: String, year: Int, month: Int) {
    val events = getMonthEventsUseCase(gId, year, month)
    _uiState.update { it.copy(dayEvents = ...) }  // 여기서도 변경
}

fun reloadEvents() {
    viewModelScope.launch {
        loadEvents(...)  // 내부에서 상태가 바뀌는지 시그니처만 봐서는 알 수 없음
    }
}

// ✅ private 함수는 결과만 반환 — 상태 변경은 public 함수에서만
private suspend fun fetchEvents(gId: String, year: Int, month: Int): EventsData {
    val events = getMonthEventsUseCase(gId, year, month)
    val tz = TimeZone.currentSystemDefault()
    val grouped = events.groupBy { it.startAt.toLocalDateTime(tz).date.day }
    return EventsData(
        calendarEvents = grouped.mapValues { (_, v) -> v.map { e -> e.toCalendarDayEvent() } },
        dayEvents = grouped.mapValues { (_, v) -> v.map { e -> e.toProfileEvent() } },
    )
}

fun reloadEvents() {
    val gId = _uiState.value.groupId ?: return
    val state = _uiState.value
    viewModelScope.launch {
        runCatching { fetchEvents(gId, state.selectedYear, state.selectedMonth) }
            .onSuccess { data ->
                _uiState.update { it.copy(calendarEvents = data.calendarEvents, dayEvents = data.dayEvents) }
            }
    }
}
```

**예외:** `init` 블록에서의 초기 로딩은 사실상 public 진입점이므로 허용한다.

---

## 7. 상태 설계 원칙 — 변하지 않는 것들

UiState의 타입 설계가 달라져도 다음 상태 설계 원칙은 변하지 않는다:

### 7.1 UI = f(State)

모든 UI 신호는 UiState에 인코딩한다. 네비게이션 의도, 에러 토스트, 다이얼로그 노출 여부 모두 포함된다.

```kotlin
// ViewModel에 콜백을 전달하지 않는다
fun save(onDone: () -> Unit)  // 금지

// 상태로 표현한다
fun save()  // 완료 시 _uiState.update { it.copy(event = Event.Saved) }
```

### 7.2 이벤트 소비

이벤트를 UiState에 담으면 한 번만 처리되도록 소비해야 한다:

```kotlin
// RouteScreen
LaunchedEffect(uiState.event) {
    val e = uiState.event ?: return@LaunchedEffect
    when (e) {
        is ProfileEvent.Saved -> onBack()
        is ProfileEvent.ShowError -> showToast(e.message)
    }
    viewModel.consumeEvent()
}
```

### 7.3 ViewModel은 상태를 방출하고, 실행은 RouteScreen이 한다

ViewModel은 "무엇을 해야 한다"를 상태로 표현하고, 실제 실행(네비게이션, 토스트 등)은 RouteScreen이 상태를 관찰하여 수행한다.

### 7.4 RouteScreen에서 데이터 레이어를 직접 호출하지 않는다

업로드, API 호출 등은 반드시 ViewModel을 거친다.

### 7.5 로컬 UI 상태도 가능하면 UiState로

`remember { mutableStateOf(...) }`는 ViewModel이 모르는 상태를 만든다. 테스트 불가, 상태 복원 불가의 원인이 된다. 비즈니스 의미가 있는 상태는 UiState에 둔다.

예외: 애니메이션 오프셋처럼 렌더링 전용이고 비즈니스 의미가 없는 값은 로컬 상태를 허용한다.

---

## 8. 결론

sealed interface는 강력한 도구이지만, UiState에 무분별하게 적용하면 그 자체가 복잡성의 원천이 된다. ViewModel의 모든 함수에서 캐스팅이 반복되고, 이를 해결하기 위한 패턴이 연쇄적으로 필요해지며, 동시성 처리와 충돌한다.

핵심 원칙은 하나다:

> **sealed interface는 화면 전체의 구조가 바뀌는 전환에만 사용한다.**  
> **그 외의 모든 상태는 단일 data class의 필드로 표현한다.**

이 원칙을 따르면 캐스팅이 사라지고, 동시성 처리가 자연스러워지며, "모든 것을 상태로" 표현하는 철학이 코드 구조와 일치하게 된다.
