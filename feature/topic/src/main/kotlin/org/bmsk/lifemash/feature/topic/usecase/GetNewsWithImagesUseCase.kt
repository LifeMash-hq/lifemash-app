package org.bmsk.lifemash.feature.topic.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.bmsk.lifemash.core.model.NewsModel
import javax.inject.Inject

internal interface GetNewsWithImagesUseCase {
    suspend operator fun invoke(newsModels: List<NewsModel>): List<NewsModel>
}

internal class GetNewsWithImagesUseCaseImpl @Inject constructor(
    private val getNewsImageUrlUseCase: GetNewsImageUrlUseCase
) : GetNewsWithImagesUseCase {
    override suspend fun invoke(newsModels: List<NewsModel>): List<NewsModel> {
        return withContext(Dispatchers.IO) {
            newsModels.map { news ->
                async {
                    val imageUrl = getNewsImageUrlUseCase(news.link)
                    news.copy(imageUrl = imageUrl)
                }
            }.awaitAll()
        }
    }
}