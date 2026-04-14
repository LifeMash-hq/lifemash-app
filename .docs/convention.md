# LifeMash-App 코딩 컨벤션

> 모듈 구조·네이밍·금지 규칙은 `CLAUDE.md` 참조.  
> 이 문서는 **"왜 이렇게 짜는가"** — 아키텍처 철학과 패턴을 다룬다.

---

## 1. 상태 설계 — 모든 것을 상태로

`UI = f(State)` 를 철저히 따른다.

- 네비게이션 의도, 에러 토스트, 다이얼로그 노출 여부 등 **모든 UI 신호는 UiState 안에** 인코딩한다
- `SharedFlow` / `Channel` 같은 별도 이벤트 스트림을 두지 않는다
- ViewModel은 상태를 방출하고, **실행(네비게이션 등)은 RouteScreen 책임**이다

```kotlin
// ❌ ViewModel에 콜백 전달
fun save(onDone: () -> Unit)

// ✅ 상태로 표현, RouteScreen에서 관찰
fun save()  // 완료 시 uiState.event = SavedEvent

// RouteScreen
LaunchedEffect(uiState.event) {
    when (uiState.event) {
        is Event.Saved -> onBack()
        else -> Unit
    }
    viewModel.consumeEvent()
}
```

---

## 2. Loaded-param 패턴 — 타입으로 전제조건을 표현한다

어떤 함수가 특정 상태에서만 호출 가능하다면, 그 상태를 **파라미터 타입으로 강제**한다.

```kotlin
// ❌ 내부에서 조건 체크
fun toggleFollow(userId: String) {
    if (uiState.value !is ProfileUiState.Loaded) return
    // ...
}

// ✅ 호출 가능 조건이 시그니처에 드러남
fun toggleFollow(state: ProfileUiState.Loaded, userId: String) {
    // state는 이미 Loaded가 보장됨
}
```

**핵심 목적은 전제조건을 타입으로 표현하는 것이다:**

- 함수 시그니처만 봐도 "Loaded 상태에서만 호출 가능"임이 드러남
- 잘못된 상태에서 호출하면 컴파일 타임에 막힘 — 버그가 숨지 않음
- 다음 개발자가 맥락 없이도 전제조건을 이해함

`_uiState.value`를 내부에서 읽는 것이 **더 적합한 경우**도 있다.  
"지금 이 작업을 실행해도 되는가"처럼 **현재 시점의 상태를 확인해야 하는 guard** 는 내부에서 읽는 것이 맞다:

```kotlin
fun toggleFollow(state: ProfileUiState.Loaded, userId: String) {
    // 중복 요청 방어 — 현재 시점 기준으로 확인해야 함
    val current = _uiState.value
    if (current is ProfileUiState.Loaded && current.isFollowInProgress) return
    // ...
}
```

---

## 3. RouteScreen에서 데이터 레이어를 직접 호출하지 않는다

업로드, API 호출 등 데이터 레이어 작업은 반드시 ViewModel을 거친다.

```kotlin
// ❌ RouteScreen에서 직접
val uploadService: UploadService = koinInject()
scope.launch { uploadService.upload(uri) }

// ✅ ViewModel에 위임
onPickImage = { uri -> viewModel.uploadAndUpdateImage(uri) }
```

---

## 4. 로컬 UI 상태도 가능하면 UiState로

`remember { mutableStateOf(...) }` 는 ViewModel이 모르는 상태를 만든다.  
테스트 불가, 재진입 시 리셋, 상태 복원 불가 등의 문제가 생긴다.

```kotlin
// ❌ RouteScreen 로컬 상태
var showFollowSheet by remember { mutableStateOf(false) }

// ✅ UiState 안으로
data class Loaded(
    // ...
    val isFollowSheetVisible: Boolean = false,
)
```

예외: 애니메이션 오프셋처럼 **렌더링 전용이고 비즈니스 의미가 없는** 값은 로컬 상태도 허용한다.

---

## 5. 이벤트 소비 패턴

이벤트를 UiState에 담으면 "한 번만 처리"를 보장해야 한다.

```kotlin
// UiState
data class XxxUiState(
    val event: XxxEvent? = null,
)

sealed interface XxxEvent {
    data object NavigateBack : XxxEvent
    data class ShowError(val message: String) : XxxEvent
}

// ViewModel
fun consumeEvent() {
    _uiState.update { it.copy(event = null) }
}

// RouteScreen
LaunchedEffect(uiState.event) {
    val e = uiState.event ?: return@LaunchedEffect
    when (e) {
        is XxxEvent.NavigateBack -> onBack()
        is XxxEvent.ShowError -> showToast(e.message)
    }
    viewModel.consumeEvent()
}
```

`LaunchedEffect(uiState.event)` 는 event 값이 바뀔 때만 재실행되므로 중복 처리되지 않는다.

---

## 6. Rich UiState — UiState가 자기 규칙의 주인이다

UiState는 빈약한 데이터 홀더가 아니라, 검증·필터링·파생 속성을 스스로 갖는 Rich Model이다.  
ViewModel은 규칙을 모르고 상태 전이만 위임한다.

자세한 내용은 [Rich UiState](rich-uistate.md) 참조.
