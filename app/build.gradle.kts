plugins {
    id("lifemash.android.application")
    id("lifemash.android.hilt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
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

    implementation(projects.shared.navigation)
    implementation(projects.shared.webview)

    implementation(libs.androidx.appcompat)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
}
