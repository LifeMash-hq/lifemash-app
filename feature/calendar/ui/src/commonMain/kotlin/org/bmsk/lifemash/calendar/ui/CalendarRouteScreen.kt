package org.bmsk.lifemash.calendar.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CalendarRouteScreen(
    onShowErrorSnackbar: (Throwable?) -> Unit,
    viewModel: CalendarViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    CalendarScreen(
        uiState = uiState,
        onDateSelect = viewModel::selectDate,
        onPrevMonth = {
            val (y, m) = if (uiState.currentMonth == 1) {
                uiState.currentYear - 1 to 12
            } else {
                uiState.currentYear to uiState.currentMonth - 1
            }
            viewModel.changeMonth(y, m)
        },
        onNextMonth = {
            val (y, m) = if (uiState.currentMonth == 12) {
                uiState.currentYear + 1 to 1
            } else {
                uiState.currentYear to uiState.currentMonth + 1
            }
            viewModel.changeMonth(y, m)
        },
    )
}
