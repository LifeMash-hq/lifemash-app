package org.bmsk.lifemash.feature.shared.common

import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberAddToCalendarLauncher(): (
    title: String,
    startMillis: Long,
    endMillis: Long?,
    location: String?,
    description: String?,
) -> Unit {
    val context = LocalContext.current
    return remember {
        { title, startMillis, endMillis, location, description ->
            val intent = Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI).apply {
                putExtra(CalendarContract.Events.TITLE, title)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                endMillis?.let { putExtra(CalendarContract.EXTRA_EVENT_END_TIME, it) }
                location?.let { putExtra(CalendarContract.Events.EVENT_LOCATION, it) }
                description?.let { putExtra(CalendarContract.Events.DESCRIPTION, it) }
            }
            context.startActivity(intent)
        }
    }
}
