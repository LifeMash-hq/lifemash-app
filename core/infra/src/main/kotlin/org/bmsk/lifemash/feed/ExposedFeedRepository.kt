package org.bmsk.lifemash.feed

import org.bmsk.lifemash.db.tables.*
import org.bmsk.lifemash.model.feed.FeedCommentDto
import org.bmsk.lifemash.model.feed.FeedPostDto
import org.bmsk.lifemash.model.feed.FeedResponse
import org.bmsk.lifemash.model.moment.MediaItemDto
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

        val rows = momentBaseQuery()
            .where {
                (Moments.authorId inList authorIds) and
                    (Moments.visibility eq "public")
            }
            .orderBy(Moments.createdAt, SortOrder.DESC)
            .let { if (offset > 0) it.offset(offset) else it }
            .limit(limit)
            .toList()

        buildFeedResponse(rows, javaUserId, offset, limit)
    }

    override fun getAllFeed(cursor: String?, limit: Int): FeedResponse = transaction {
        val offset = cursor?.toLongOrNull() ?: 0L

        val rows = momentBaseQuery()
            .where { Moments.visibility eq "public" }
            .orderBy(Moments.createdAt, SortOrder.DESC)
            .let { if (offset > 0) it.offset(offset) else it }
            .limit(limit)
            .toList()

        buildFeedResponse(rows, requesterId = null, offset, limit)
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

        val rows = momentBaseQuery()
            .where { Moments.id inList trendingIds }
            .toList()

        val mediaByMoment = loadMediaByMoments(rows.map { it[Moments.id] })
        rows.map { it.toFeedPost(requesterId = null, mediaByMoment) }
    }

    // ── private helpers ──

    private fun momentBaseQuery() =
        Moments
            .join(Users, JoinType.INNER, Moments.authorId, Users.id)
            .join(Events, JoinType.LEFT) { Moments.eventId eq Events.id }
            .selectAll()

    private fun loadMediaByMoments(momentIds: List<java.util.UUID>): Map<java.util.UUID, List<MediaItemDto>> {
        if (momentIds.isEmpty()) return emptyMap()
        return MomentMedia.selectAll()
            .where { MomentMedia.momentId inList momentIds }
            .orderBy(MomentMedia.sortOrder, SortOrder.ASC)
            .groupBy { it[MomentMedia.momentId] }
            .mapValues { (_, rows) ->
                rows.map { r ->
                    MediaItemDto(
                        mediaUrl = r[MomentMedia.mediaUrl],
                        mediaType = r[MomentMedia.mediaType],
                        sortOrder = r[MomentMedia.sortOrder],
                        width = r[MomentMedia.width],
                        height = r[MomentMedia.height],
                        durationMs = r[MomentMedia.durationMs],
                    )
                }
            }
    }

    private fun buildFeedResponse(
        rows: List<ResultRow>,
        requesterId: java.util.UUID?,
        offset: Long,
        limit: Int,
    ): FeedResponse {
        val mediaByMoment = loadMediaByMoments(rows.map { it[Moments.id] })
        val items = rows.map { it.toFeedPost(requesterId, mediaByMoment) }
        val nextCursor = if (items.size == limit) (offset + limit).toString() else null
        return FeedResponse(items = items, nextCursor = nextCursor)
    }

    private fun ResultRow.toFeedPost(
        requesterId: java.util.UUID?,
        mediaByMoment: Map<java.util.UUID, List<MediaItemDto>>,
    ): FeedPostDto {
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

        val commentCount = if (eventId != null) {
            Comments.selectAll().where { Comments.eventId eq eventId }.count().toInt()
        } else 0

        val commentPreview = if (eventId != null) {
            Comments
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
        } else emptyList()

        return FeedPostDto(
            id = momentId.toString(),
            authorId = this[Moments.authorId].toString(),
            authorNickname = this[Users.nickname],
            authorProfileImage = this[Users.profileImage],
            eventId = eventId?.toString(),
            eventTitle = this.getOrNull(Events.title),
            media = mediaByMoment[momentId] ?: emptyList(),
            caption = this[Moments.caption],
            likeCount = likeCount,
            isLiked = isLiked,
            commentPreview = commentPreview,
            commentCount = commentCount,
            createdAt = this[Moments.createdAt].toString(),
        )
    }
}
