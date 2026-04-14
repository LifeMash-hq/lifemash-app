package org.bmsk.lifemash.feed.impl.di

import org.bmsk.lifemash.domain.usecase.feed.CreateFeedCommentUseCase
import org.bmsk.lifemash.domain.usecase.feed.GetFeedCommentsUseCase
import org.bmsk.lifemash.domain.usecase.feed.GetFeedUseCase
import org.bmsk.lifemash.domain.usecase.feed.ToggleFeedLikeUseCase
import org.bmsk.lifemash.feed.impl.FeedViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val feedUiModule = module {
    viewModel {
        FeedViewModel(
            GetFeedUseCase(get()),
            ToggleFeedLikeUseCase(get()),
            GetFeedCommentsUseCase(get()),
            CreateFeedCommentUseCase(get()),
        )
    }
}
