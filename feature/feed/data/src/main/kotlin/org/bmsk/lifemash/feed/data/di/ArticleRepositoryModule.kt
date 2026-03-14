package org.bmsk.lifemash.feed.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.bmsk.lifemash.feed.data.repository.ArticleRepositoryImpl
import org.bmsk.lifemash.feed.domain.repository.ArticleRepository
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
