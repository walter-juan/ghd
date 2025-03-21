# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
### Changed
### Deprecated
### Removed
### Fixed
### Security

## [2.0.4]
### Added
- Third-Party software and licenses screen
- Add local logs to the app
- Support for creating pre-release versions
### Changed
- Add repository using the full GitHub URL instead of owner and name
- Undo split code in Gradle modules
### Fixed
- Fix dismissed reviews notification

## [2.0.4-beta03]
### Changed
- Add repository using the full GitHub URL instead of owner and name
- Undo split code in Gradle modules
### Removed
- Repository search when adding a new one
### Fixed
- Fix dismissed reviews notification
- Fix pre-release option from the create release action

## [2.0.4-beta02]
### Added
- Repository search when adding a new one
### Fixed
- Fix pre-release option from the create release action

## [2.0.4-beta01]
### Added
- Third-Party software and licenses screen
- Add local logs to the app
- Support for creating pre-release versions

## [2.0.3]
### Fixed
- 🐛 Fix crash with i18n classes

## [2.0.2]
### Added
- Notification when review from you is dismissed
### Changed
- Split code in Gradle modules
- Removed "Published at" prefix from the release card
- Split date from the status in the pull request card
- Show the dates in the pull request card by the state they have
### Fixed
- Add missing debug ICNS icon
- Change icon when the app is running on debug mode
- Allow multiple instances of the app when running on debug mode

## [2.0.1]
### Changed
- Order the releases by group then by published date
### Fixed
- 🐛 Fix crash when the repos are empty
- Update app dialog

## [2.0.0]
### Added
- Confirmation before delete a repository
- Koin as dependency injection
- `openBrowser` flag to open browser automatically after run `dependencyUpdates`
### Changed
- 🎨 New application design
- Default synchronization interval changed from 1 to 5 minutes
- Update dependencies
### Removed
- Mark as seen
- Login screen
- `Dependency updates` GitHub action
- MaterialColors

## [1.6.0]
### Added
- Add the API rate limit in pull requests and releases top bar
### Changed
- Notifications behaviour
  - ⚠️ Check the repositories screen and the new notifications screen to update according to your preferences, now you have:
    1. Better notifications per pull request, now you can filter by username, state, activity, etc. see this in the new Notifications screen
    2. Split enable/disable pull requests and releases between enabled/disable synchronization and enable/disable notifications
- Pull request card
  - Icons changed for a more descriptive ones
  - Show if the pull request can be merged (mergeable)
- Pull request seen behaviour
  - Shows a small circle in the icons to indicate the changes since seen
  - Shows if the code has been changed since seen
- Update dependencies
### Removed
- Notifications for release updates
### Fixed
- Ensures that only one instance of the application can run at the same time

## [1.5.4]
### Added
- Be able to filter the pull requests notifications by state
### Changed
- Pull request created notifications renamed to pull request state changed notifications
- Pull request updated notifications renamed to pull request activity notifications
### Fixed
- Pull request activity always sent notifications when they were never marked as seen

## [1.5.3]
### Changed
- Use icons from an external dependency

## [1.5.2]
### Added
- Group the list of repos by group

## [1.5.1]
### Changed
- Use multiplatform setting library
- Update dependencies
- Update GitHub Actions
- Remove database entities and use directly the domain ones
- Increment to 25 the pull requests to retrieve
### Fixed
- Pull requests synchronization

## [1.5.0]
### ⚠️ IMPORTANT ⚠️
- The database has been replaced. Before upgrading export the repositories and update the exported YAML to import them later.
### Changed
- Replace Exposed for Room multiplatform
- Replace Voyager for ViewModel and Jetpack Navigation multiplatform
- Replace [Octicons](https://github.com/primer/octicons/) for [Tabler](https://github.com/tabler/tabler-icons) icons
- Replace [Remix](https://github.com/Remix-Design/remixicon/) for [Tabler](https://github.com/tabler/tabler-icons) icons
### Security
- 😔 Database encryption removed, everything stored as plain text

## [1.4.2]
### Fixed
- Trim data when import and before save to avoid errors
### Changed
- Update dependencies

## [1.4.1]
### Changed
- Update dependencies
- Internal changes

## [1.4.0]
### ⚠️ IMPORTANT ⚠️
- The database has been replaced. Before upgrading export the repositories and update the exported YAML to import them later.
- After updating the app, it will prompt you to "open" it, as if a database already exists, but that won't be the case. Instead, you should click the "fresh start" button to reset everything.
### Added
- Add the `ghdCleanDebugAppFolder` gradle task to clean the debug app folder
- Error handling during synchronization
- Enable pull requests or releases features from repos
- New screen with an example of the YAML file used for the bulk import of repositories
### Changed
- Update dependencies and Ruby version
- Sort pull request first by state, then by seen and finally by the created date
- Notifications now are joined all together in the settings screen
- Decrement the retrieved pull requests from 50 to 10 to improve the performance
- Change how to know if the DB is encrypted
### Removed
- Dependency updates scheduled workflow, now only the manual one is available
- Feature previews removed
### Security
- Resolve a high severity [Dependabot alert](https://github.com/walter-juan/ghd/security/dependabot/6)

## [1.3.4]
### Changed
- Update dependencies
- Hide the extras from a PR when it's mark as seen
- Update to Java 17
### Security
- Resolve a high severity [Dependabot alert](https://github.com/walter-juan/ghd/security/dependabot/5) on `gradle/gradle-build-action`.

## [1.3.3]
### Changed
- Update the octicons and remixicon
### Fixed
- From download icon scripts, strip non word characters from folders
### Security
- Update Ruby Gems (reported as security risks by GitHub)

## [1.3.2]
### Added
- Add the reviewers, status and mergeable to pull request card
### Changed
- Background colors
### Fixed
- Some colors

## [1.3.1]
### Added
- New card designs for pull requests, releases and repos to check
- Add new designs feature previews
### Changed
- Replace [iconoir](https://iconoir.com/) for [remix icon](https://remixicon.com/)
- Replace [material icons](https://fonts.google.com/icons) for [remix icon](https://remixicon.com/)
### Fixed
- Dialog theme

## [1.3.0]
### Changed
- Use Material 3
- Change color scheme
- Some minor visual changes like the synchronization text, now is in the toolbar
### Fixed
- Select input colors

## [1.2.6]
### Changed
- Database encryption now is optional

## [1.2.5]
### Added
- Add release notes in GitHub release
### Fixed
- Synchronization time improvement

## [1.2.4]
### Fixed
- System default theme
### Changed
- Technical changes
  - Re-implement the screen component
  - Create an outlined select field component
  - Add small delay before publish the synchronized event
  - Replace use cases in favour of services
  - Add database migrations
  - Replace logger class

## [1.2.3]
### Added
- Check app updates

## [1.2.2]
### Fixed
- Remove the pull requests that doesn't match with the branch regex from the database
### Changed
- Upload compressed binaries
- Update dependencies

## [1.2.1]
### Added
- Upload macOS and Windows builds on GitHub Releases
- Add the number of comments in pull requests
### Changed
- Set the pull requests as the first screen after open the app instead of the about
- Update dependencies
### Fixed
- Retrieve the latest 50 open + 50 merged + 50 closed pull requests instead of the latest 50 open or merged or closed
- Sort the grouped releases by name
- Support dark theme for release and pull cards

## [1.2.0]
### Added
- Export repos to check to YAML
### Changed
- Plain text import changed for YAML
- Update dependencies

## [1.1.1]
### Changed
- GitHub Actions, rename, update versions, change the `set-output` for the new one

## [1.1.0]
### Added
- Pull request notifications in bulk import file
- Database encryption
- Add dependency updates GitHub Workflow
### Changed
- Use Gradle version catalog, now the dependencies are in [libs.versions.toml](gradle/libs.versions.toml).

## [1.0.4]
### Added
- [Voyager](https://github.com/adrielcafe/voyager) library for navigation and view models
### Changed
- Update dependencies

## [1.0.3]
### Changed
- One GitHub package action using matrix instead of two
- Create generate release data GitHub action
- Remove reusable validation GitHub workflows
- Remove the tag validation for create release action

## [1.0.2]
### Added
- [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin)

## [1.0.1]
### Fixed
- Use the `baseRefName` and `headRefName` to have it even if the ref has been deleted.

## [1.0.0]
_First version_

[unreleased]: https://github.com/walter-juan/ghd/compare/v2.0.4...dev
[2.0.4]: https://github.com/walter-juan/ghd/releases/tag/v2.0.4
[2.0.4-beta03]: https://github.com/walter-juan/ghd/releases/tag/v2.0.4-beta03
[2.0.4-beta02]: https://github.com/walter-juan/ghd/releases/tag/v2.0.4-beta02
[2.0.4-beta01]: https://github.com/walter-juan/ghd/releases/tag/v2.0.4-beta01
[2.0.3]: https://github.com/walter-juan/ghd/releases/tag/v2.0.3
[2.0.2]: https://github.com/walter-juan/ghd/releases/tag/v2.0.2
[2.0.1]: https://github.com/walter-juan/ghd/releases/tag/v2.0.1
[2.0.0]: https://github.com/walter-juan/ghd/releases/tag/v2.0.0
[1.6.0]: https://github.com/walter-juan/ghd/releases/tag/v1.5.4
[1.5.4]: https://github.com/walter-juan/ghd/releases/tag/v1.5.4
[1.5.3]: https://github.com/walter-juan/ghd/releases/tag/v1.5.3
[1.5.2]: https://github.com/walter-juan/ghd/releases/tag/v1.5.2
[1.5.1]: https://github.com/walter-juan/ghd/releases/tag/v1.5.1
[1.5.0]: https://github.com/walter-juan/ghd/releases/tag/v1.5.0
[1.4.2]: https://github.com/walter-juan/ghd/releases/tag/v1.4.2
[1.4.1]: https://github.com/walter-juan/ghd/releases/tag/v1.4.1
[1.4.0]: https://github.com/walter-juan/ghd/releases/tag/v1.4.0
[1.3.4]: https://github.com/walter-juan/ghd/releases/tag/v1.3.4
[1.3.3]: https://github.com/walter-juan/ghd/releases/tag/v1.3.3
[1.3.2]: https://github.com/walter-juan/ghd/releases/tag/v1.3.2
[1.3.1]: https://github.com/walter-juan/ghd/releases/tag/v1.3.1
[1.3.0]: https://github.com/walter-juan/ghd/releases/tag/v1.3.0
[1.2.6]: https://github.com/walter-juan/ghd/releases/tag/v1.2.6
[1.2.5]: https://github.com/walter-juan/ghd/releases/tag/v1.2.5
[1.2.4]: https://github.com/walter-juan/ghd/releases/tag/v1.2.4
[1.2.3]: https://github.com/walter-juan/ghd/releases/tag/v1.2.3
[1.2.2]: https://github.com/walter-juan/ghd/releases/tag/v1.2.2
[1.2.1]: https://github.com/walter-juan/ghd/releases/tag/v1.2.1
[1.2.0]: https://github.com/walter-juan/ghd/releases/tag/v1.2.0
[1.1.1]: https://github.com/walter-juan/ghd/releases/tag/v1.1.1
[1.1.0]: https://github.com/walter-juan/ghd/releases/tag/v1.1.0
[1.0.4]: https://github.com/walter-juan/ghd/releases/tag/v1.0.4
[1.0.3]: https://github.com/walter-juan/ghd/releases/tag/v1.0.3
[1.0.2]: https://github.com/walter-juan/ghd/releases/tag/v1.0.2
[1.0.1]: https://github.com/walter-juan/ghd/releases/tag/v1.0.1
[1.0.0]: https://github.com/walter-juan/ghd/releases/tag/v1.0.0
