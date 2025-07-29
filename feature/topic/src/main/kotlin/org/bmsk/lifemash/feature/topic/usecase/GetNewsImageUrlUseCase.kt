package org.bmsk.lifemash.feature.topic.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import org.jsoup.Jsoup
import javax.inject.Inject

internal interface GetNewsImageUrlUseCase {
    suspend operator fun invoke(link: String): String
}

internal class GetNewsImageUrlUseCaseImpl @Inject constructor() : GetNewsImageUrlUseCase {
    override suspend fun invoke(link: String): String {
        return Dispatchers.IO {
            Jsoup.connect(link).get().select("meta[property=og:image]").attr("content")
        }
    }
}