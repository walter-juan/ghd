
> This is a personal project created to solve a need and play with it, try some libraries, etc.
>
> That said, **I'm not responsible if one day the app stops working** or doesn't work properly and you need to re-install it. **This should not happen**, but if a re-installation is needed it will be written in the release notes.

# GHD

[GHD (GitHub Dashboard)](https://github.com/walter-juan/ghd), is an application to show the pull requests and releases from your GitHub repositories.

Features:
- List open & draft pull requests
  - Mark a pull request as seen to know later if it has been updated
  - Notify when pull request is created or updated
  - Filter pull request by branch
- List latest releases from a repository
  - Notify when a new release is created
- Light and dark themes

☀️ Light Theme | 🌒 Dark Theme
:------:|:------:|
<img width="400" alt="ghd-pull-requests-light" src="https://user-images.githubusercontent.com/4141614/216827850-6eb5b4f6-712a-48bc-add6-c1168c238175.png">|<img width="400" alt="ghd-pull-requests-dark" src="https://user-images.githubusercontent.com/4141614/216827846-93378e20-d598-42d0-a15e-435f4c5e0a77.png">
<img width="400" alt="ghd-releases-light" src="https://user-images.githubusercontent.com/4141614/216827857-2ac3ebc8-a280-4d74-b189-28f47753dc12.png">|<img width="400" alt="ghd-releases-dark" src="https://user-images.githubusercontent.com/4141614/216827855-f6dd87c4-2085-48c1-98c2-4a9b082c0235.png">


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
- Clean the debug app folder
    ```shell
    ./gradlew ghdCleanDebugAppFolder
    ```
- Create the packages
    ```shell
    ./gradlew packageDistributionForCurrentOS
    ```
- To know which dependencies have updates
    ```shell
    ./gradlew dependencyUpdates
    ```
- Update Gradle version
    ```shell
    ./gradlew wrapper --gradle-version 7.5.1
    ```
# Scripts

The folder `scripts` contains some scripts in Ruby:

- Scripts must be executed from the scripts folder
    ```shell
    cd scripts
    ```
- Download octicons
    ```shell
    bundle exec ruby octicons-download.rb
    ```
- Download remixicon
    ```shell
    bundle exec ruby remixicon-download.rb
    ```

# Database

The database used in this project is [H2](https://www.h2database.com/) because of encryption support + embedded. As the database is encrypted a user and password is required, you can find it in [DbSettings](app/src/main/kotlin/com/woowla/ghd/data/local/db/DbSettings.kt).

**H2 Commands**

- [`SCRIPT`](https://www.h2database.com/html/commands.html#script): Creates a SQL script from the database.

**H2 Console**

For more detailed information follow [the tutorial from H2](https://www.h2database.com/html/tutorial.html).

1. Download H2 console app:
   - The `Platform-Independent Zip` from [the website](https://www.h2database.com/html/download.html)
   - Or `h2-<version>.jar` from [the GitHub releases](https://github.com/h2database/h2database/releases)
2. From terminal run the h2 `$ java -jar h2*.jar` and the web browser will be opened.
3. In the [DbSettings](app/src/main/kotlin/com/woowla/ghd/data/local/db/DbSettings.kt) you can find all the data required like the URL, driver, user and password

# Links
- GitHub
  - [GitHub Actions runner images](https://github.com/actions/runner-images)
- Compose Multiplatform
  - [Landing page](https://www.jetbrains.com/lp/compose-multiplatform/)
  - [GitHub repo](https://github.com/JetBrains/compose-multiplatform)
  - [Desktop template](https://github.com/JetBrains/compose-multiplatform-desktop-template/)
  - [Samples](https://github.com/JetBrains/compose-multiplatform/blob/master/examples/README.md)
  - [Tutorials](https://github.com/JetBrains/compose-multiplatform/blob/master/tutorials/README.md)
- Android official
  - [Material Design Components](https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary) code documentation, the package `androidx.compose.material`
  - [Material Design 3 Components](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary) code documentation, the package `androidx.compose.material3`
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
  - [Octicons](https://primer.style/octicons/), [Octicons GitHub](https://github.com/primer/octicons/)
  - [Remix icon](https://remixicon.com/), [Remix icon GitHub](https://github.com/Remix-Design/remixicon/)
  - [Google material icons](https://fonts.google.com/icons)
- Colors
  - More or less all of them should be in [MaterialColors](app/src/main/kotlin/com/woowla/ghd/utils/MaterialColors.kt)
  - [Material colors](https://material.io/design/color/the-color-system.html#tools-for-picking-colors), other resource [materialui](https://materialui.co/colors)
- Others
  - [Boxy SVG editor](https://boxy-svg.com/)
  - [Coroutine-based solution for delayed and periodic work](https://gist.github.com/gmk57/67591e0c878cedc2a318c10b9d9f4c0c) 
  - [Composables](https://www.composables.com/)
