package org.bmsk.lifemash.error

import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.bmsk.lifemash.feature.shared.error.ErrorReporter

class CrashlyticsErrorReporter : ErrorReporter {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun report(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }

    override fun log(message: String) {
        crashlytics.log(message)
    }
}
