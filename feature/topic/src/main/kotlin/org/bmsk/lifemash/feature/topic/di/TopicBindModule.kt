package org.bmsk.lifemash.feature.topic.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import org.bmsk.lifemash.feature.topic.api.TopicNavController
import org.bmsk.lifemash.feature.topic.api.TopicNavGraph
import org.bmsk.lifemash.feature.topic.navigation.TopicNavControllerImpl
import org.bmsk.lifemash.feature.topic.navigation.TopicNavGraphImpl

@Module
@InstallIn(ActivityComponent::class)
internal abstract class TopicBindModule {

    @Binds
    abstract fun bindTopicNavController(
        dataSource: TopicNavControllerImpl
    ): TopicNavController

    @Binds
    abstract fun bindTopicNavGraph(
        dataSource: TopicNavGraphImpl
    ): TopicNavGraph
}
