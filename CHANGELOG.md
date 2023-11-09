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

## [1.3.5]
### Added
- Add the `ghdCleanDebugAppFolder` gradle task to clean the debug app folder
### Changed
- Update dependencies and Ruby version
- Sort pull request first by seen, then by state and finally by the created date
- Notifications now are joined all together in the settings screen
- Decrement the retrieved pull requests from 50 to 10 to improve the performance
- ⚠️ ⚠️ Change how to know if the DB is encrypted. This will require a fresh start removing the old database ⚠️ ⚠️
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

[unreleased]: https://github.com/walter-juan/ghd/compare/v1.3.5...dev
[1.3.5]: https://github.com/walter-juan/ghd/releases/tag/v1.3.5
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
