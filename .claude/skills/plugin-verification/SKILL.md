---
name: plugin-verification
description: verifyPlugin IDE matrix in plugin/build.gradle.kts. Use for the pluginVerification block, adding/removing a verified IDE, libs.versions.idea, sinceBuild/untilBuild.
---

# Plugin Verification Rules

The `pluginVerification { ides { … } }` block in `plugin/build.gradle.kts` is the authoritative IDE
matrix for `verifyPlugin`. It MUST be kept up to date.

* The Gradle target `verifyPlugin` MUST NEVER be executed (see rule `development`). Only the user may run it.

## IDE Matrix

* IDEs are registered with `create(<IntelliJPlatformType>, <version>)` on the `ides` receiver.
  There is NO `ide(...)` function in the IntelliJ Platform Gradle Plugin.
* The matrix MUST contain at least these IDEs:
  * IntelliJ IDEA (`IntelliJPlatformType.IntellijIdea`, unified `IU`)
  * Rider (`IntelliJPlatformType.Rider`)
  * CLion (`IntelliJPlatformType.CLion`)
  * GoLand (`IntelliJPlatformType.GoLand`)
* Adding another supported IDE MUST start with adding it to this matrix.
* Removing an IDE from the matrix MUST be confirmed with the user first.

## Versions

* The verification version MUST be read from the version catalog: `libs.versions.idea`
  (`gradle/libs.versions.toml`).
* Hardcoded IDE version strings inside the matrix are FORBIDDEN — the matrix must never be able to
  drift away from the platform the plugin is compiled against.
* Bumping `idea` in the version catalog therefore bumps every verified IDE automatically; after such a
  bump the user MUST be told that `verifyPlugin` has to be run by them.

## Compatibility Range

* Whenever `sinceBuild` or `untilBuild` in `intellijPlatform.pluginConfiguration.ideaVersion` changes,
  the matrix MUST be re-checked against the new range.

## Result

* `verifyPlugin` is expected to pass without errors for EVERY IDE in the matrix; the user verifies this.
* Warnings are acceptable, except DEPRECATION and REMOVAL warnings — those MUST be fixed once reported.
