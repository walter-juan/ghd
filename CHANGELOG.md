# 1.1.1
- Changed
  - GitHub Actions, rename and update versions
# 1.1.0
- Added
  - Pull request notifications in bulk import file
  - Database encryption
  - Add dependency updates GitHub Workflow
- Changed
  - Use Gradle version catalog, now the dependencies are in [libs.versions.toml](gradle/libs.versions.toml). 
  
# 1.0.4
- Added
  - [Voyager](https://github.com/adrielcafe/voyager) library for navigation and view models
- Changed
  - Update dependencies

# 1.0.3
- Changed
  - One GitHub package action using matrix instead of two
  - Create generate release data GitHub action
  - Remove reusable validation GitHub workflows
  - Remove the tag validation for create release action

# 1.0.2
- Added
  - [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin)

# 1.0.1
- Fixed
  - Use the `baseRefName` and `headRefName` to have it even if the ref has been deleted.

# 1.0.0
- First version
