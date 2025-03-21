> [!NOTE]
> This is a personal project created to solve a need and play with it, try some libraries, etc.
>
> That said, **I'm not responsible if one day the app stops working** or doesn't work properly and you need to re-install it. **This should not happen**, but if a re-installation is needed it will be written in the release notes.

> [!CAUTION]
> Migrated to Compose navigation, view models and Room KMP with non-final versions it could be unstable
>
> Things to solve in a future:
>   - [ ] Whole database encryption removed, PAT will be stored as plain text
>   - [ ] Relations with [Intermediate data class](https://developer.android.com/training/data-storage/room/relationships#data-class), seems not to be working (search for `TODO relations`)

# GHD

[GHD (GitHub Dashboard)](https://github.com/walter-juan/ghd), is an application to show the pull requests and releases from your GitHub repositories.

Features:
- List of the pull requests
- List latest releases from a repository
- Notifications
- Light and dark themes

☀️ Light Theme | 🌒 Dark Theme
:------:|:------:|
<img width="350" src="https://github.com/user-attachments/assets/3740f75c-5105-44cc-b93c-4b84044b7a36">|<img width="350" src="https://github.com/user-attachments/assets/4d566f57-04ef-4af9-ac0b-27a6f5680401">
<img width="350" src="https://github.com/user-attachments/assets/91436ec4-6dd8-4b2d-966a-ac4a41a65322">|<img width="350" src="https://github.com/user-attachments/assets/61e76de6-6d66-4bba-825e-9a3c20ca136c">
<img width="350" src="https://github.com/user-attachments/assets/a81b730e-04a1-4ce1-abca-1055329a4294">|<img width="350" src="https://github.com/user-attachments/assets/3010fc5b-6f35-4649-97ff-3c4604f92f0e">

# Installation

## Windows application

Download the `exe` file from [releases](https://github.com/walter-juan/ghd/releases/latest) and install.

## macOS application

The **macOS application is unsigned** and to be able to run the `com.apple.quarantine` has to be removed. Right now, as this app is more for personal use, I don't want to register and do all the stuff about signing with Apple.

> The `com.apple.quarantine` attribute is used to determine if an application should be checked (and blocked if needed.)

So if you want to use this app you should remove the `com.apple.quarantine`.

**How to install**

1. Download the `dmg` file from [releases](https://github.com/walter-juan/ghd/releases/latest)
2. Open the `dmg` file and move the application to `Applications` folder
3. Open terminal and run:
  ```shell
  xattr -d com.apple.quarantine /Applications/ghd.app
  ```

**Sources**

- [Der Flounder, Clearing the quarantine extended attribute from downloaded applications](https://derflounder.wordpress.com/2012/11/20/clearing-the-quarantine-extended-attribute-from-downloaded-applications/)
- [sureshg/compose-desktop-sample GitHub Project](https://github.com/sureshg/compose-desktop-sample)
- [OSX Daily, How to Fix App “is damaged and can’t be opened. You should move it to the Trash” Error on Mac](https://osxdaily.com/2019/02/13/fix-app-damaged-cant-be-opened-trash-error-mac/)
- [How-To Geek, How to Fix “App Is Damaged and Can’t Be Opened” on Mac](https://www.howtogeek.com/803598/app-is-damaged-and-cant-be-opened/)

**Commands**

- Show the attributes
  ```shell
  xattr /path/to/my-app.app
  ```

- Remove the `com.apple.quarantine` attribute
  ```shell
  xattr -d com.apple.quarantine /path/to/my-app.app
  ```

- Remove the `com.apple.quarantine` attribute recursively for the entire targeted .app directory contents
  ```shell
  sudo xattr -dr com.apple.quarantine /path/to/my-app.app
  ```

- Remove the all attributes + recursively
  ```shell
  sudo xattr -cr /path/to/my-app.app
  ```

# IntelliJ IDEA plugins

- [Kotest](https://plugins.jetbrains.com/plugin/14080-kotest)
- [Detekt](https://plugins.jetbrains.com/plugin/10761-detekt)

# Gradle commands

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
- Clean the debug app folder
    ```shell
    ./gradlew ghdCleanDebugAppFolder
    ```
- Create the packages
    ```shell
    ./gradlew packageDistributionForCurrentOS
    ```
- To know which dependencies have updates, you can use `openBrowser` to open directly the browser with the HTML report:
    ```shell
    ./gradlew dependencyUpdates
    ./gradlew dependencyUpdates -PopenBrowser=true
    ```
- Run static code analysis:
    ```shell
    ./gradlew detekt
    ```
- Update Gradle version
    ```shell
    ./gradlew wrapper --gradle-version latest
    ./gradlew wrapper --gradle-version 7.5.1
    ```
# Project

## Detekt
Config file is in the root of the project, [config/detekt/detekt-config.ym](config/detekt/detekt-config.yml)

# Links
- GitHub
  - [GitHub Actions runner images](https://github.com/actions/runner-images)
  - [GitHub GraphQL schema](https://docs.github.com/en/graphql/overview/public-schema)
- Compose Multiplatform
  - [Landing page](https://www.jetbrains.com/lp/compose-multiplatform/)
  - [GitHub repo](https://github.com/JetBrains/compose-multiplatform)
  - [Desktop template](https://github.com/JetBrains/compose-multiplatform-desktop-template/)
  - [Samples](https://github.com/JetBrains/compose-multiplatform/blob/master/examples/README.md)
  - [Tutorials](https://github.com/JetBrains/compose-multiplatform/blob/master/tutorials/README.md)
- Android official
  - [Material Design Components](https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary) code documentation, the package `androidx.compose.material`
  - [Material Design 3 Components](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary) code documentation, the package `androidx.compose.material3`
  - [List of Compose modifiers](https://developer.android.com/develop/ui/compose/modifiers-list)
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
  - [Tabler](https://tabler.io/icons), [Tabler GitHub](https://github.com/tabler/tabler-icons)
  - [Google material icons](https://fonts.google.com/icons)
- Colors
  - More or less all of them should be in [MaterialColors](app/src/main/kotlin/com/woowla/ghd/utils/MaterialColors.kt)
  - [Material colors](https://material.io/design/color/the-color-system.html#tools-for-picking-colors), other resource [materialui](https://materialui.co/colors)
- Others
  - [Boxy SVG editor](https://boxy-svg.com/)
  - [Coroutine-based solution for delayed and periodic work](https://gist.github.com/gmk57/67591e0c878cedc2a318c10b9d9f4c0c)
  - [Composables](https://www.composables.com/)
