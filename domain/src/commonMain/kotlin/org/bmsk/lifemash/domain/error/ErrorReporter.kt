package org.bmsk.lifemash.domain.error

interface ErrorReporter {
    fun report(throwable: Throwable)
    fun log(message: String)
}
