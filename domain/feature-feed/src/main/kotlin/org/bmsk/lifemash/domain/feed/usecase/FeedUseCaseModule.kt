package org.bmsk.lifemash.domain.feed.usecase

import dagger.Module
import dagger.Provides
import org.bmsk.lifemash.domain.feed.repository.ArticleRepository

@Module
object FeedUseCaseModule {
    @Provides
    fun provideGetArticlesUseCase(
        repo: ArticleRepository
    ): GetArticlesUseCase = GetArticlesUseCaseImpl(repo)
}