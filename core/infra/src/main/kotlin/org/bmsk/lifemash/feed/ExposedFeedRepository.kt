package org.bmsk.lifemash.feed

import org.bmsk.lifemash.db.tables.*
import org.bmsk.lifemash.model.feed.FeedCommentDto
import org.bmsk.lifemash.model.feed.FeedPostDto
import org.bmsk.lifemash.model.feed.FeedResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class ExposedFeedRepository : FeedRepository {

    override fun getFeed(userId: Uuid, cursor: String?, limit: Int): FeedResponse = transaction {
        val javaUserId = userId.toJavaUuid()

        val followingIds = Follows.selectAll()
            .where { Follows.followerId eq javaUserId }
            .map { it[Follows.followingId] }

        val authorIds = followingIds + javaUserId
        val offset = cursor?.toLongOrNull() ?: 0L

        val rows = Moments
            .join(Users, JoinType.INNER, Moments.authorId, Users.id)
            .join(Events, JoinType.INNER, Moments.eventId, Events.id)
            .selectAll()
            .where {
                (Moments.authorId inList authorIds) and
                    (Moments.visibility eq "public")
            }
            .orderBy(Moments.createdAt, SortOrder.DESC)
            .let { if (offset > 0) it.offset(offset) else it }
            .limit(limit)
            .toList()

        val items = rows.map { it.toFeedPost(javaUserId) }
        val nextCursor = if (items.size == limit) (offset + limit).toString() else null

        FeedResponse(items = items, nextCursor = nextCursor)
    }

    override fun getTrending(limit: Int): List<FeedPostDto> = transaction {
        val likeCountExpr = Likes.momentId.count()

        val trendingIds = Likes
            .select(Likes.momentId, likeCountExpr)
            .groupBy(Likes.momentId)
            .orderBy(likeCountExpr, SortOrder.DESC)
            .limit(limit)
            .map { it[Likes.momentId] }

        if (trendingIds.isEmpty()) return@transaction emptyList()

        Moments
            .join(Users, JoinType.INNER, Moments.authorId, Users.id)
            .join(Events, JoinType.INNER, Moments.eventId, Events.id)
            .selectAll()
            .where { Moments.id inList trendingIds }
            .toList()
            .map { it.toFeedPost(requesterId = null) }
    }

    private fun ResultRow.toFeedPost(requesterId: java.util.UUID?): FeedPostDto {
        val momentId = this[Moments.id]
        val eventId = this[Moments.eventId]

        val likeCount = Likes.selectAll()
            .where { Likes.momentId eq momentId }
            .count().toInt()

        val isLiked = if (requesterId != null) {
            Likes.selectAll()
                .where { (Likes.userId eq requesterId) and (Likes.momentId eq momentId) }
                .count() > 0
        } else false

        val commentCount = Comments.selectAll()
            .where { Comments.eventId eq eventId }
            .count().toInt()

        val commentPreview = Comments
            .join(Users, JoinType.INNER, Comments.authorId, Users.id)
            .selectAll()
            .where { Comments.eventId eq eventId }
            .orderBy(Comments.createdAt, SortOrder.DESC)
            .limit(2)
            .map { c ->
                FeedCommentDto(
                    authorNickname = c[Users.nickname],
                    content = c[Comments.content],
                )
            }

        return FeedPostDto(
            id = momentId.toString(),
            authorId = this[Moments.authorId].toString(),
            authorNickname = this[Users.nickname],
            authorProfileImage = this[Users.profileImage],
            eventId = eventId.toString(),
            eventTitle = this[Events.title],
            imageUrl = this[Moments.imageUrl],
            caption = this[Moments.caption],
            likeCount = likeCount,
            isLiked = isLiked,
            commentPreview = commentPreview,
            commentCount = commentCount,
            createdAt = this[Moments.createdAt].toString(),
        )
    }
}
