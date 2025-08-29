package org.bmsk.lifemash.data.article.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.bmsk.lifemash.data.article.repository.ArticleRepositoryImpl
import org.bmsk.lifemash.domain.feed.repository.ArticleRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ArticleRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindArticleRepository(
        impl: ArticleRepositoryImpl,
    ): ArticleRepository
}
