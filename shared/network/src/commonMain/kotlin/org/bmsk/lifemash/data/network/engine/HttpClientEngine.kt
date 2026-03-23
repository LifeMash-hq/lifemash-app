package org.bmsk.lifemash.data.network.engine

import io.ktor.client.engine.HttpClientEngine

expect fun createPlatformHttpClientEngine(): HttpClientEngine
