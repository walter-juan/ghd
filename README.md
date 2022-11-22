
> ℹ️ **This project is a sample to play with it, not an application to use**
>
> This is a personal project created to solve a need and play with it, try some libraries, etc.
>
> That said, I'm not responsible if one day the app stops working, I don't do some database migration, or something else that makes the app not work properly

[GHD (GitHub Dashboard)](https://github.com/walter-juan/ghd), is an application to show the pull requests and releases from your GitHub repositories.

# Useful Gradle commands

**By default, the debug flag is enabled**, example how to remove it:
```shell
$ ./gradlew build -PdebugConfig=false
```

- Run the app
    ```shell
    ./gradlew run
    ```
- Build the project
    ```shell
    ./gradlew build
    ```
- Clean the project
    ```shell
    ./gradlew clean
    ```
- Create the packages
    ```shell
    ./gradlew package
    ```
- To know which dependencies have updates
    ```shell
    ./gradlew dependencyUpdates
    ```
- Update Gradle version
    ```shell
    ./gradlew wrapper --gradle-version 7.5.1
    ```
# H2 Database

The database used in this project is [H2](https://www.h2database.com/) because of encryption support + embedded. As the database is encrypted a user and password is required, you can find it in [DbSettings](app/src/main/kotlin/com/woowla/ghd/data/local/db/DbSettings.kt).

**H2 Console**

For more detailed information follow [the tutorial from H2](https://www.h2database.com/html/tutorial.html).

1. Download H2 console app:
   - The `Platform-Independent Zip` from [the website](https://www.h2database.com/html/download.html)
   - Or `h2-<versuin>.jar` from [the GitHub releases](https://github.com/h2database/h2database/releases)
2. From terminal run the h2 `$ java -jar h2*.jar` and the web browser will be opened.
3. In the [DbSettings](app/src/main/kotlin/com/woowla/ghd/data/local/db/DbSettings.kt) you can find all the data required like the URL, driver, user and password

# Links
- GitHub
  - [GitHub Actions runner images](https://github.com/actions/runner-images)
- Android official
  - [Material Design Components](https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary) code documentation, the package `androidx.compose.material`
  - [List and id's for performance](https://developer.android.com/jetpack/compose/lists)
  - [State](https://developer.android.com/jetpack/compose/state)
  - [Kotlin for compose](https://developer.android.com/jetpack/compose/kotlin)
  - [Custom theming](https://developer.android.com/jetpack/compose/themes/custom)
  - [CompositionLocal](https://developer.android.com/jetpack/compose/compositionlocal)
  - [Compose pathway](https://developer.android.com/courses/pathways/compose)
- Awesome lists
  - [Awesome Jetpack Compose Learning Resources](https://github.com/androiddevnotes/awesome-jetpack-compose-learning-resources)
  - [Awesome Jetpack compose](https://github.com/Naveentp/Awesome-Jetpack-Compose)
- Compose samples
  - [Android offical compose samples](https://github.com/android/compose-samples)
  - [Gurupreet/ComposeCookBook](https://github.com/Gurupreet/ComposeCookBook)
  - [Gurupreet/ComposeSpotifyDesktop](https://github.com/Gurupreet/ComposeSpotifyDesktop)
- Icons
  - [Octicons](https://primer.style/octicons/)
  - [iconoir](https://iconoir.com/)
  - [Google material icons](https://fonts.google.com/icons)
- Colors
  - More or less all of them should be in [MaterialColors](src/main/kotlin/com/woowla/ghd/utils/MaterialColors.kt)
  - [Material colors](https://material.io/design/color/the-color-system.html#tools-for-picking-colors), other resource [materialui](https://materialui.co/colors)
- Others
  - [Boxy SVG editor](https://boxy-svg.com/)
  - [Coroutine-based solution for delayed and periodic work](https://gist.github.com/gmk57/67591e0c878cedc2a318c10b9d9f4c0c) 