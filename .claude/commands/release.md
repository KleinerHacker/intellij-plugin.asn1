# Release Preparation

Release preparation for the ASN.1 IntelliJ plugin.

The argument `$ARGUMENTS` is the **target version** for this release (e.g. `1.1.0`). Its presence
means the user is starting release preparation towards exactly that version - treat every following
step in this turn as part of that release prep, not as an unrelated version-number edit.

If `$ARGUMENTS` is empty or is not a plain version number (`<major>.<minor>.<patch>`, optionally with a
suffix such as `-SNAPSHOT`), ask the user for the target version before doing anything else.

All changes MUST BE run WITHOUT ANY PLAN.

## What counts as a release version number

A **release version number** is the version of THIS plugin: the version a release is built, tagged and
published under. Everything else is NOT touched:

* Versions of third-party libraries, Gradle plugins, IDE platforms
  (`gradle/libs.versions.toml`, `id("…") version "…"` in `*.gradle.kts`, `gradle/wrapper`)
* Past releases in `CHANGELOG.md` (`## [1.0.1]`, `## [1.0.0]`, …) and every text describing them
* Placeholders and examples (`asn-1-<version>.zip`, `-Pversion=<tag>`, `[e.g. 1.0.0]` in issue
  templates, comments that mention an old release such as "how the 1.0.0 release failed")
* Format versions of data files (for example the `version="142"` of the colour schemes)
* The minimum / maximum IDE build numbers (`sinceBuild`, `untilBuild`)

The release pipelines pass the version from the Git tag (`-Pversion=${{ github.ref_name }}`), so the
tag itself is not a file in this repository.

## Steps

### 1. Determine the old version

* Read the highest released heading of `CHANGELOG.md` (the first `## [x.y.z]` below `## [Unreleased]`)
* Read `version=` in `gradle.properties` and `<version>` in `plugin/src/main/resources/META-INF/plugin.xml`
* The three values may differ (the files are not always kept in sync); remember each of them as its own
  "old value" and report them

### 2. Find ALL release version numbers

Do not rely on the list below alone; search the whole repository (excluding `build/`, `.gradle/`,
`.git/`, `node_modules/` and `.claude/plans/`) for **every** old value found in step 1 and for the
property names `version`, `pluginVersion` and `<version>`, in all file types (`*.kts`, `*.toml`,
`*.properties`, `*.xml`, `*.yml`, `*.yaml`, `*.md`, `*.json`). Classify every hit with the rules of the section
above. Known locations of release version numbers:

* `gradle.properties` - `version=`: if the current value ends with `-SNAPSHOT`, it MUST NOT be set to the
  target version but to the NEXT BUGFIX version of the target followed by `-SNAPSHOT` (target `1.1.0` ->
  `1.1.1-SNAPSHOT`), because the snapshot always denotes the upcoming development state after the release.
  Without a `-SNAPSHOT` suffix the value is set to the target version itself
* `plugin/src/main/resources/META-INF/plugin.xml` - `<version>`
* `CHANGELOG.md` - the section `## [Unreleased]`
* Any further hit that the classification marks as "version of this plugin" (documentation pages,
  README badges or install instructions, `mkdocs.yml`, pipeline files, build scripts)

If a hit cannot be classified with certainty, do not change it; list it in the report.

### 3. Change the version numbers

* Set every release version number found in step 2 to the target version `$ARGUMENTS`
* `CHANGELOG.md`: rename `## [Unreleased]` to `## [<target>]` and insert a new, empty `## [Unreleased]`
  above it; keep the existing sub-headings (`### Added`, `### Changed`, `### Fixed`) of the released
  section unchanged. If `[Unreleased]` holds no entries, say so in the report and still ask the user
  before creating an empty release section
* Content in files is written in ENGLISH
* Apply all changes to one file in ONE tool call (collect them first)
* Stage new or moved files with `git add`; never commit, tag or push

### 4. Verify

* Search the repository again for the old values from step 1 with the same exclusions; every remaining
  hit must be explainable by the "NOT touched" rules above
* Check that `gradle.properties`, `plugin.xml` and the first released heading of `CHANGELOG.md` now carry
  the same version

### 5. Report

Report to the user in German per the console-output rule:

* old values (CHANGELOG, `gradle.properties`, `plugin.xml`) and target version
* every file changed, with the line that changed
* every hit that was deliberately left untouched, with the reason
* every hit that could not be classified

## Rules

* Do not create a plan for this change
* Do not build, commit, tag or push; that stays a separate, explicit step the user asks for
* Do not modify source code, tests or the third-party versions
* Review the result against the standing rules (`.claude/CLAUDE.md`, `.claude/rules/*.md`); ask the user if
  anything is unclear
