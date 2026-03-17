plugins {
    id("lifemash.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.kotlin.android)
}

dependencies {
    implementation(projects.model)
    implementation(projects.feature.main)

    implementation(projects.feature.feed.domain)
    implementation(projects.feature.feed.data)
    implementation(projects.feature.feed.api)
    implementation(projects.feature.feed.ui)

    implementation(projects.feature.scrap.domain)
    implementation(projects.feature.scrap.data)
    implementation(projects.feature.scrap.api)
    implementation(projects.feature.scrap.ui)

    implementation(projects.feature.history.api)
    implementation(projects.feature.history.ui)

    implementation(projects.feature.calendar.domain)
    implementation(projects.feature.calendar.data)
    implementation(projects.feature.calendar.api)
    implementation(projects.feature.calendar.ui)
    implementation(projects.feature.auth.domain)
    implementation(projects.feature.auth.data)
    implementation(projects.feature.auth.api)
    implementation(projects.feature.auth.ui)
    implementation(projects.feature.notification.domain)
    implementation(projects.feature.notification.data)
    implementation(projects.feature.notification.api)
    implementation(projects.feature.notification.ui)
    implementation(projects.shared.fcm)

    implementation(projects.shared.network)
    implementation(projects.shared.webview)

    implementation(libs.koin.android)
    implementation(libs.androidx.room.runtime)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.logging)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.core.ktx)
    implementation(libs.androidx.datastore.preferences)
}