package org.bmsk.lifemash.di

import org.bmsk.lifemash.assistant.AssistantRepository
import org.bmsk.lifemash.assistant.AssistantService
import org.bmsk.lifemash.assistant.AssistantUsageRepository
import org.bmsk.lifemash.assistant.ClaudeApiClient
import org.bmsk.lifemash.assistant.StubAssistantRepository
import org.bmsk.lifemash.assistant.StubAssistantService
import org.bmsk.lifemash.assistant.StubAssistantUsageRepository
import org.bmsk.lifemash.assistant.StubClaudeApiClient
import org.bmsk.lifemash.assistant.StubUserApiKeyRepository
import org.bmsk.lifemash.assistant.UserApiKeyRepository
import org.bmsk.lifemash.auth.AuthService
import org.bmsk.lifemash.auth.StubAuthService
import org.bmsk.lifemash.auth.oauth.GoogleOAuthClient
import org.bmsk.lifemash.auth.oauth.KakaoOAuthClient
import org.bmsk.lifemash.auth.oauth.StubGoogleOAuthClient
import org.bmsk.lifemash.auth.oauth.StubKakaoOAuthClient
import org.bmsk.lifemash.blocks.BlocksService
import org.bmsk.lifemash.blocks.StubBlocksService
import org.bmsk.lifemash.comment.CommentRepository
import org.bmsk.lifemash.comment.CommentService
import org.bmsk.lifemash.comment.StubCommentRepository
import org.bmsk.lifemash.comment.StubCommentService
import org.bmsk.lifemash.event.EventRepository
import org.bmsk.lifemash.event.EventService
import org.bmsk.lifemash.event.StubEventRepository
import org.bmsk.lifemash.event.StubEventService
import org.bmsk.lifemash.group.GroupRepository
import org.bmsk.lifemash.group.GroupService
import org.bmsk.lifemash.group.StubGroupRepository
import org.bmsk.lifemash.group.StubGroupService
import org.bmsk.lifemash.marketplace.MarketplaceRepository
import org.bmsk.lifemash.marketplace.MarketplaceService
import org.bmsk.lifemash.marketplace.StubMarketplaceRepository
import org.bmsk.lifemash.marketplace.StubMarketplaceService
import org.bmsk.lifemash.user.StubUserRepository
import org.bmsk.lifemash.user.UserRepository
import org.koin.dsl.module

val stubModule = module {
    single<AuthService> { StubAuthService() }
    single<GroupService> { StubGroupService() }
    single<EventService> { StubEventService() }
    single<CommentService> { StubCommentService() }
    single<AssistantService> { StubAssistantService() }
    single<BlocksService> { StubBlocksService() }
    single<MarketplaceService> { StubMarketplaceService() }
    single<UserRepository> { StubUserRepository() }
    single<GroupRepository> { StubGroupRepository() }
    single<EventRepository> { StubEventRepository() }
    single<CommentRepository> { StubCommentRepository() }
    single<AssistantRepository> { StubAssistantRepository() }
    single<AssistantUsageRepository> { StubAssistantUsageRepository() }
    single<UserApiKeyRepository> { StubUserApiKeyRepository() }
    single<MarketplaceRepository> { StubMarketplaceRepository() }
    single<KakaoOAuthClient> { StubKakaoOAuthClient() }
    single<GoogleOAuthClient> { StubGoogleOAuthClient() }
    single<ClaudeApiClient> { StubClaudeApiClient() }
}
