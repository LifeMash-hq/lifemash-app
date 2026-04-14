# Rich UiState — 빈약한 데이터 홀더에서 벗어나기

> UiState는 단순한 데이터 홀더가 아니라, 자기 규칙을 가진 Rich Model이다.

---

## 문제 — 빈약한 UiState

```kotlin
data class ProfileEditUiState(
    val username: String,
    val isSaving: Boolean,
    // ...
)
```

검증, 필터링, 파생 속성 로직이 전부 ViewModel에 흩어진다:

```kotlin
// ViewModel
fun updateUsername(value: String) {
    val filtered = value.filter { it.isLetterOrDigit() || it == '_' || it == '.' }
    _uiState.update { it.copy(username = filtered) }
}

fun save() {
    val state = _uiState.value
    if (state.username.any { !it.isLetterOrDigit() && it != '_' && it != '.' }) return
    if (state.name.isBlank()) return
    if (state.isSaving) return
    // ...
}
```

- 같은 규칙이 여러 함수에 중복된다
- ViewModel을 거쳐야만 규칙을 테스트할 수 있다
- UiState만 봐서는 어떤 값이 유효한지 알 수 없다

---

## 원칙 — UiState가 자기 규칙의 주인이다

### 1. 변환 함수(`withXxx`)로 입력을 정제한다

잘못된 값이 상태에 들어갈 수 없도록 UiState가 입력을 필터링한다:

```kotlin
fun withUsername(value: String): ProfileEditUiState {
    val filtered = value.filter { it.isLetterOrDigit() || it == '_' || it == '.' }
    return copy(username = filtered)
}
```

ViewModel은 규칙을 모르고 위임만 한다:

```kotlin
fun updateUsername(value: String) {
    _uiState.update { it.withUsername(value) }
}
```

### 2. 파생 속성으로 검증 결과를 노출한다

```kotlin
val isUsernameValid: Boolean by lazy {
    username.all { it.isLetterOrDigit() || it == '_' || it == '.' }
}

val isSaveEnabled: Boolean by lazy {
    name.isNotBlank() && isUsernameValid && !isSaving
}
```

ViewModel의 가드절이 단순해진다:

```kotlin
fun save() {
    if (!_uiState.value.isSaveEnabled) return
    // ...
}
```

### 3. 파생 속성은 `by lazy`로 캐싱한다

UiState는 `data class`(immutable)이므로 한 번 계산된 값은 변하지 않는다. `by lazy`로 캐싱하면:

- **리컴포지션 시 이점**: 같은 인스턴스를 여러 컴포저블이 읽을 때 1회만 계산
- **`copy()` 후에는 새 인스턴스**: lazy가 다시 초기화되므로 stale 값 문제 없음

```kotlin
// 하나의 리컴포지션에서 같은 인스턴스를 여러 곳이 읽음
SaveButton(enabled = uiState.isSaveEnabled)     // 1번째 접근 → 계산
TopBar(showSave = uiState.isSaveEnabled)         // 2번째 접근 → 캐시 히트
```

`get()`은 매번 재계산하므로, 접근 횟수가 많거나 연산 비용이 있을 때 `by lazy`가 유리하다.

---

## 전체 예시

```kotlin
data class ProfileEditUiState(
    val name: String,
    val username: String,
    val bio: String,
    val profileImageUrl: String?,
    val defaultSubTab: Int,
    val myCalendarView: Int,
    val othersCalendarView: Int,
    val defaultVisibility: Int,
    val isSaving: Boolean,
    val error: String?,
    val event: ProfileEditEvent?,
) {
    val isUsernameValid: Boolean by lazy {
        username.all { it.isLetterOrDigit() || it == '_' || it == '.' }
    }

    val isSaveEnabled: Boolean by lazy {
        name.isNotBlank() && isUsernameValid && !isSaving
    }

    fun withName(value: String): ProfileEditUiState = copy(name = value)

    fun withUsername(value: String): ProfileEditUiState {
        val filtered = value.filter { it.isLetterOrDigit() || it == '_' || it == '.' }
        return copy(username = filtered)
    }

    fun withBio(value: String): ProfileEditUiState = copy(bio = value)

    companion object {
        val Default = ProfileEditUiState(
            name = "",
            username = "",
            bio = "",
            profileImageUrl = null,
            defaultSubTab = 0,
            myCalendarView = 0,
            othersCalendarView = 0,
            defaultVisibility = 0,
            isSaving = false,
            error = null,
            event = null,
        )
    }
}
```

```kotlin
// ViewModel — 규칙을 모르고 위임만
fun updateUsername(value: String) {
    _uiState.update { it.withUsername(value) }
}

fun save() {
    if (!_uiState.value.isSaveEnabled) return
    // ...
}
```

---

## 요약

| | 빈약한 UiState | Rich UiState |
|---|---|---|
| 검증/필터링 위치 | ViewModel에 흩어짐 | UiState 안에 응집 |
| ViewModel 역할 | 규칙 + 상태 관리 | 상태 전이만 |
| 테스트 | ViewModel 테스트 필요 | 순수 data class 단위 테스트 |
| 파생 속성 캐싱 | 불가 (ViewModel 함수) | `by lazy` |
| 재사용 | ViewModel에 종속 | UiState만 가져다 쓸 수 있음 |
