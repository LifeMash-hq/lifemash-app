# LifeMash News App

LifeMash is a modern, modular news application for Android, built with the latest technologies to provide a seamless and engaging news reading experience.

<div align="center">
  <img src="gif/lifemash.gif" height="600">
</div>

## ✨ Features

*   **News Feed:** Fetches and displays news from various sources.
*   **Topic-based Filtering:** Allows users to filter news by specific topics.
*   **Article Scraping:** Saves articles for offline reading.
*   **WebView Integration:** Provides an integrated web browsing experience for reading full articles.

## 🛠 Tech Stack & Architecture

*   **Modern Toolkit:** Built with the latest Android technologies, including [Kotlin](https://kotlinlang.org/), [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html), and [Flow](https://developer.android.com/kotlin/flow).
*   **UI:** The UI is built with [Jetpack Compose](https://developer.android.com/jetpack/compose), using [Material Design 3](https://m3.material.io/) for a modern and responsive user experience.
*   **Architecture:** The app follows a modularized architecture, with a clear separation of concerns between UI, domain, and data layers. It uses the MVVM (Model-View-ViewModel) pattern.
*   **Dependency Injection:** [Hilt](https://dagger.dev/hilt/) is used for dependency injection to manage dependencies and improve testability.
*   **Networking:** [Retrofit](https://square.github.io/retrofit/) and [OkHttp](https://square.github.io/okhttp/) are used for efficient network operations. [Jsoup](https://jsoup.org/) is used for HTML parsing, and [Tikxml](https://github.com/Tickaroo/tikxml) for parsing XML-based RSS feeds.
*   **Image Loading:** [Landscapist](https://github.com/skydoves/landscapist) and [Glide](https://github.com/bumptech/glide) are used for efficient image loading and caching.
*   **Testing:** The app has a comprehensive test suite, including unit tests ([JUnit](https://junit.org/junit4/), [MockK](https://mockk.io/), [Mockito](https://site.mockito.org/), [Kotest](https://kotest.io/)).
*   **Code Quality:** [Detekt](https://detekt.github.io/detekt/) is used for static code analysis to ensure a high-quality codebase.

## 🚀 Building and Running

To build and run the app, you will need Android Studio.

1.  Clone the repository: `git clone https://github.com/YiBeomSeok/LifeMash-NewsApp.git`
2.  Open the project in Android Studio.
3.  Build and run the app on an emulator or a physical device.

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.