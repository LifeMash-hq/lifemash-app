package org.bmsk.lifemash.auth.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.component.LifeMashButton
import org.bmsk.lifemash.designsystem.component.LifeMashButtonStyle
import org.bmsk.lifemash.designsystem.component.LifeMashInput
import org.bmsk.lifemash.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing

@Composable
internal fun AuthScreen(
    uiState: AuthUiState,
    onBackClick: () -> Unit,
    onKakaoSignIn: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onEmailSignIn: (email: String, password: String) -> Unit,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        // 탑바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(LifeMashSpacing.md),
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp),
            ) {
                Text(
                    text = "←",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                text = "로그인",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        // 스크롤 영역
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = LifeMashSpacing.xxl),
            verticalArrangement = Arrangement.Center,
        ) {
            // 소셜 로그인 그룹
            Column(
                verticalArrangement = Arrangement.spacedBy(LifeMashSpacing.md),
            ) {
                // 카카오 버튼
                Button(
                    onClick = onKakaoSignIn,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(LifeMashRadius.md),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFEE500),
                        contentColor = Color(0xFF191919),
                    ),
                    enabled = uiState !is AuthUiState.Loading,
                ) {
                    Text(
                        text = "카카오로 로그인",
                        style = MaterialTheme.typography.titleSmall,
                    )
                }

                // Google 버튼
                Button(
                    onClick = onGoogleSignIn,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(LifeMashRadius.md),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                    ),
                    enabled = uiState !is AuthUiState.Loading,
                ) {
                    Text(
                        text = "Google로 로그인",
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }

            Spacer(modifier = Modifier.height(LifeMashSpacing.xxl))

            // "또는" 디바이더
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(LifeMashSpacing.md),
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
                Text(
                    text = "또는",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }

            Spacer(modifier = Modifier.height(LifeMashSpacing.xl))

            // 이메일 폼
            Column(
                verticalArrangement = Arrangement.spacedBy(LifeMashSpacing.md),
            ) {
                LifeMashInput(
                    value = email,
                    onValueChange = { email = it },
                    label = "이메일",
                    placeholder = "hello@example.com",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = uiState !is AuthUiState.Loading,
                )

                LifeMashInput(
                    value = password,
                    onValueChange = { password = it },
                    label = "비밀번호",
                    placeholder = "비밀번호 입력",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = uiState is AuthUiState.Error,
                    errorMessage = (uiState as? AuthUiState.Error)?.message,
                    enabled = uiState !is AuthUiState.Loading,
                )

                // "비밀번호를 잊었나요?" 링크
                Box(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = { },
                        modifier = Modifier.align(Alignment.CenterEnd),
                    ) {
                        Text(
                            text = "비밀번호를 잊었나요?",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(LifeMashSpacing.xl))

            // "로그인" 버튼
            LifeMashButton(
                text = "로그인",
                onClick = { onEmailSignIn(email.trim(), password) },
                modifier = Modifier.fillMaxWidth(),
                style = LifeMashButtonStyle.Primary,
                isLoading = uiState is AuthUiState.Loading,
                enabled = email.isNotBlank() && password.isNotBlank(),
            )

            // 푸터
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = LifeMashSpacing.lg),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("계정이 없으신가요? ")
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        ) {
                            append("회원가입")
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(LifeMashSpacing.xxxl))
        }
    }
}
