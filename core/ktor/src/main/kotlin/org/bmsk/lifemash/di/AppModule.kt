package org.bmsk.lifemash.di

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.bmsk.lifemash.assistant.*
import org.bmsk.lifemash.assistant.tools.CalendarTool
import org.bmsk.lifemash.auth.AuthService
import org.bmsk.lifemash.auth.AuthServiceImpl
import org.bmsk.lifemash.auth.oauth.*
import org.bmsk.lifemash.comment.CommentRepository
import org.bmsk.lifemash.comment.CommentService
import org.bmsk.lifemash.comment.CommentServiceImpl
import org.bmsk.lifemash.comment.ExposedCommentRepository
import org.bmsk.lifemash.memo.ChecklistItemRepository
import org.bmsk.lifemash.memo.ExposedChecklistItemRepository
import org.bmsk.lifemash.memo.ExposedMemoRepository
import org.bmsk.lifemash.memo.MemoRepository
import org.bmsk.lifemash.memo.MemoService
import org.bmsk.lifemash.memo.MemoServiceImpl
import org.bmsk.lifemash.event.EventRepository
import org.bmsk.lifemash.event.EventService
import org.bmsk.lifemash.event.EventServiceImpl
import org.bmsk.lifemash.event.ExposedEventRepository
import org.bmsk.lifemash.group.ExposedGroupRepository
import org.bmsk.lifemash.group.GroupRepository
import org.bmsk.lifemash.group.GroupService
import org.bmsk.lifemash.group.GroupServiceImpl
import org.bmsk.lifemash.group.MembershipGuard
import org.bmsk.lifemash.group.MembershipGuardImpl
import org.bmsk.lifemash.explore.ExposedExploreRepository
import org.bmsk.lifemash.explore.ExploreRepository
import org.bmsk.lifemash.explore.ExploreService
import org.bmsk.lifemash.feed.ExposedFeedRepository
import org.bmsk.lifemash.feed.FeedRepository
import org.bmsk.lifemash.feed.FeedService
import org.bmsk.lifemash.follow.ExposedFollowRepository
import org.bmsk.lifemash.follow.FollowRepository
import org.bmsk.lifemash.follow.FollowService
import org.bmsk.lifemash.like.ExposedLikeRepository
import org.bmsk.lifemash.like.LikeRepository
import org.bmsk.lifemash.like.LikeService
import org.bmsk.lifemash.moment.ExposedMomentRepository
import org.bmsk.lifemash.moment.MomentRepository
import org.bmsk.lifemash.moment.MomentService
import org.bmsk.lifemash.notification.FcmService
import org.bmsk.lifemash.notification.FirebaseFcmService
import org.bmsk.lifemash.profile.ExposedProfileRepository
import org.bmsk.lifemash.profile.ProfileRepository
import org.bmsk.lifemash.profile.ProfileService
import org.bmsk.lifemash.notification.ExposedNotificationRepository
import org.bmsk.lifemash.notification.NotificationRepository
import org.bmsk.lifemash.notification.NotificationService
import org.bmsk.lifemash.upload.S3Config
import org.bmsk.lifemash.upload.S3UploadService
import org.bmsk.lifemash.upload.UploadService
import org.bmsk.lifemash.user.ExposedUserRepository
import org.bmsk.lifemash.user.UserRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val coreModule: Module = module {
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    single<UserRepository> { ExposedUserRepository() }
    single<GroupRepository> { ExposedGroupRepository() }
    single<EventRepository> { ExposedEventRepository() }
    single<CommentRepository> { ExposedCommentRepository() }
    single<MemoRepository> { ExposedMemoRepository() }
    single<ChecklistItemRepository> { ExposedChecklistItemRepository() }

    single<KakaoOAuthClient> { HttpKakaoOAuthClient(get()) }
    single<GoogleOAuthClient> { HttpGoogleOAuthClient(get()) }
    single<FcmService> { FirebaseFcmService(get()) }

    single<AuthService> { AuthServiceImpl(get(), get(), get()) }
    single<GroupService> { GroupServiceImpl(get()) }
    single<MembershipGuard> { MembershipGuardImpl(get()) }
    single<EventService> { EventServiceImpl(get(), get(), get()) }
    single<CommentService> { CommentServiceImpl(get(), get(), get()) }
    single<MemoService> { MemoServiceImpl(get(), get(), get(), get()) }

    single<AssistantRepository> { ExposedAssistantRepository() }
    single<AssistantUsageRepository> { ExposedAssistantUsageRepository() }
    single<UserApiKeyRepository> { ExposedUserApiKeyRepository() }
    single<ClaudeApiClient> { HttpClaudeApiClient(get()) }
    single { CalendarTool(get(), get(), get()) }
    single { ToolRegistry(get()) }
    single { ExternalToolExecutor(get()) }

    // 소셜 기능
    single<FollowRepository> { ExposedFollowRepository() }
    single<MomentRepository> { ExposedMomentRepository() }
    single<ProfileRepository> { ExposedProfileRepository() }
    single<FeedRepository> { ExposedFeedRepository() }
    single<LikeRepository> { ExposedLikeRepository() }
    single<NotificationRepository> { ExposedNotificationRepository() }
    single<ExploreRepository> { ExposedExploreRepository() }
    single { NotificationService(get()) }
    single { FollowService(get(), get()) }
    single { MomentService(get()) }
    single { ProfileService(get()) }
    single { FeedService(get()) }
    single { LikeService(get()) }
    single { ExploreService(get(), get()) }
    single { S3Config.fromEnv() }
    single<UploadService> { S3UploadService(get()) }
}
