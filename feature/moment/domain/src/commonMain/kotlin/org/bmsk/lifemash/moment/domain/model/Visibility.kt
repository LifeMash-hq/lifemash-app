package org.bmsk.lifemash.moment.domain.model

enum class Visibility(val value: String) {
    PUBLIC("public"),
    FOLLOWERS("followers"),
    PRIVATE("private"),
    ;

    companion object {
        fun next(current: Visibility): Visibility {
            val values = entries
            return values[(values.indexOf(current) + 1) % values.size]
        }
    }
}
