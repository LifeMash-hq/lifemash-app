package org.bmsk.lifemash.feed.domain.model

enum class FeedFilter(val label: String, val queryValue: String) {
    ALL("전체", "all"),
    FOLLOWING("팔로잉", "following"),
    RECOMMENDED("추천", "recommended"),
}
