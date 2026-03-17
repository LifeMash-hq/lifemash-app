package org.bmsk.lifemash.feature.shared.error

interface ErrorReporter {
    fun report(throwable: Throwable)
    fun log(message: String)
}
