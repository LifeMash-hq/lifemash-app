package org.bmsk.lifemash.designsystem.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.compositionLocalOf

/**
 * Scaffold의 innerPadding을 하위 트리에 전달하기 위한 CompositionLocal.
 *
 * ### 제공처
 * - [org.bmsk.lifemash.designsystem.component.AdaptiveNavigation] — CompactLayout(Scaffold)에서 provide
 * - 확장 레이아웃(ExpandedLayout)은 bottom bar 없으므로 기본값 PaddingValues() 유지
 *
 * ### 소비 방식
 * - LazyList 계열: `contentPadding = LocalScaffoldPadding.current`
 * - 일반 Column/Box: `Modifier.padding(LocalScaffoldPadding.current)`
 *
 * ### 주의
 * - Scaffold 바깥(ExpandedLayout, 다이얼로그 등)에서는 기본값(0)이 반환됨
 * - feature 모듈에서 직접 PaddingValues를 하드코딩하지 말 것
 */
val LocalScaffoldPadding = compositionLocalOf { PaddingValues() }
