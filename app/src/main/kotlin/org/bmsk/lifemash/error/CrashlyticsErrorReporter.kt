package org.bmsk.lifemash.error

import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.bmsk.lifemash.domain.error.ErrorReporter

class CrashlyticsErrorReporter : ErrorReporter {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun report(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }

    override fun log(message: String) {
        crashlytics.log(message)
    }
}
