plugins {
    id("lifemash.android.application")
    id("lifemash.android.hilt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.feature.main)
    implementation(projects.feature.mainNavGraph)
    implementation(projects.feature.scrap)
    implementation(projects.feature.webview)
    implementation(projects.feature.feed)

    implementation(projects.domain.featureFeed)
    implementation(projects.domain.featureHistory)
    implementation(projects.domain.featureScrap)

    implementation(projects.data.core)
    implementation(projects.data.article)
    implementation(projects.data.history)
    implementation(projects.data.scrap)
    implementation(projects.data.search)

    implementation(libs.androidx.appcompat)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
}
