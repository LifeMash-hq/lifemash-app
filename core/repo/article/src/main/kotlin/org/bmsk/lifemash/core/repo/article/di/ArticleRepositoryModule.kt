package org.bmsk.lifemash.core.repo.article.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.bmsk.lifemash.core.repo.article.ArticleRepositoryImpl
import org.bmsk.lifemash.core.repo.article.api.ArticleRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ArticleRepositoryModule {

    @Binds
    @Singleton
    abstract fun provideArticleRepository(
        impl: ArticleRepositoryImpl,
    ): ArticleRepository
}