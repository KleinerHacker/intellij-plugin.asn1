/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.testing.TestFilter
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.time.Duration

// Target IDE version — single source of truth in gradle/libs.versions.toml (registered as the "libs"
// catalog via buildSrc/settings.gradle.kts).
val ideaVersion = extensions.getByType<VersionCatalogsExtension>()
    .named("libs").findVersion("idea").get().requiredVersion

// The IntelliJ Platform ships its own kotlin-stdlib as an IDE jar (not a resolved Gradle module), so it
// does NOT participate in dependency conflict resolution. Transitive dependencies (jackson-module-kotlin
// pulls kotlin-reflect → kotlin-stdlib) therefore decide the version on the test/runtime classpath. If
// that resolves to a stdlib older than the compiler (2.2.20+ emits @DebugMetadata version 2), the
// platform's coroutine debug probes crash with "Debug metadata version mismatch. Expected: 1, got 2",
// which kills the plugin-descriptor-loading workers and hangs every platform test until the Gradle
// timeout. Pin the Kotlin artifacts to the catalog version (matching both the compiler and the bundled
// 2.4.0 stdlib) so the running stdlib understands the metadata the compiler emits.
val kotlinVersion = extensions.getByType<VersionCatalogsExtension>()
    .named("libs").findVersion("kotlin").get().requiredVersion

// Convention for the language module (:language): an IntelliJ
// Platform *module* (not a publishable plugin) sharing the Kotlin/Jackson/IDE/test setup. The actual
// publishable plugin (:plugin) configures org.jetbrains.intellij.platform itself.
plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform.module")
    `maven-publish`
}

kotlin {
    // Compile with JDK 25: since IntelliJ 2026.2 the platform jars are Java 25 (class file 69), so an
    // older javac/kotlinc cannot even read them. The emitted bytecode is pinned to Java 21 below so the
    // plugin still loads on the whole supported IDE range (sinceBuild 263).
    jvmToolchain(25)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

// Publish the language modules (:language) as regular Maven artifacts to
// the GitHub Packages registry. Wired into the release workflow after all other release steps but before
// the GitHub release page is created. Credentials come from the CI environment (GITHUB_ACTOR/GITHUB_TOKEN).
publishing {
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
            artifactId = "asn-1-${project.name}"
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            // GitHub Packages requires the URL path to match the actual owner/repo; GITHUB_REPOSITORY is
            // set by CI ("owner/repo"), otherwise fall back to the known repository name.
            val repository = System.getenv("GITHUB_REPOSITORY") ?: "KleinerHacker/intellij-plugin.asn1"
            url = uri("https://maven.pkg.github.com/$repository")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

intellijPlatform {
    instrumentCode = false
}

// Single source of truth for the target IDE across all modules: a local IDE when configured (Gradle property
// `localIdePath` or env `LOCAL_IDE_PATH`), otherwise the downloaded SDK. Pointing this at an IDE whose build
// differs from the target version makes the platform tests hang during app boot. Resolved once here because
// both the `intellijPlatform` dependencies and the custom test suites below need it.
val localIdePath: String? = (providers.gradleProperty("localIdePath").orNull
    ?: providers.environmentVariable("LOCAL_IDE_PATH").orNull)?.takeIf { it.isNotBlank() }

dependencies {
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
        implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    }

    testImplementation("junit:junit:4.13.2")

    intellijPlatform {
        if (localIdePath != null) {
            local(localIdePath)
        } else {
            intellijIdea(ideaVersion)
        }
        testFramework(TestFrameworkType.Platform)

    }
}

tasks.withType<Test>().configureEach {
    // Platform tests log through java.util.logging (JUL) via TestLoggerFactory — NOT log4j. The old
    // idea/log4j.xml + idea.log.level were silently ignored. Two independent knobs actually matter:
    //   1. intellij.console.log.level — the IntelliJ Platform Gradle plugin sets this to "warning" by
    //      default; it is the threshold of the JUL console handler that echoes WARN+ records to stdout.
    //      Override it to "off" to silence the console entirely (records still go to the per-test idea.log).
    //   2. idea.log.config.file — read as a JUL .properties file and loaded into the LogManager.
    // Note: on a FAILED test the framework additionally dumps the full buffered debug log (down to FINE/
    // TRACE) to stderr for diagnostics — that is independent of the above and only appears for failures.
    systemProperty("intellij.console.log.level", "off")
    systemProperty("idea.log.config.file", "${rootDir}/gradle/test-logging.properties")
    // On a FAILED test the framework dumps the full buffered debug log (down to FINE/TRACE) somewhere.
    // Default target is stderr (floods the console); with this flag it goes to a per-test file under the
    // sandbox log dir and only a short "Log saved to: …" line is printed. Keeps the console quiet even
    // while tests are failing.
    systemProperty("idea.split.test.logs", "true")
    // Hard backstop so a hung test cannot stall the build indefinitely (mirrors the former root config).
    timeout.set(Duration.ofMinutes(15))
}

// ─────────────────────────────────────────────────────────────────────────────────────────────
// The two test suites — see the root build.gradle.kts for the convention. `test` keeps running everything
// (that is what `check` depends on); these two are the filtered entry points.
//
// Registered through `intellijPlatformTesting.testIde` rather than as plain Test tasks: the platform plugin
// does not configure `test` in place but supplies it through a dedicated `prepareTest` task, so a hand-rolled
// Test task gets no sandbox and no platform system properties. `testIde` produces a TestIdeTask — a Test
// subclass that goes through the same preparation — and is the only supported way to add a second test task.
// The block above therefore applies to these tasks as well, since they are Test tasks.
// ─────────────────────────────────────────────────────────────────────────────────────────────
// Both resolved against the project — inside the `task { }` block below the receiver is the task itself.
val testSourceSet = sourceSets[SourceSet.TEST_SOURCE_SET_NAME]

fun registerTestSuite(name: String, taskDescription: String, applyFilter: TestFilter.() -> Unit) {
    intellijPlatformTesting.testIde.register(name) {
        if (localIdePath != null) {
            localPath.set(file(localIdePath))
        } else {
            type.set(IntelliJPlatformType.IntellijIdea)
            version.set(ideaVersion)
        }

        task {
            group = LifecycleBasePlugin.VERIFICATION_GROUP
            description = taskDescription

            // `testIde` supplies the IntelliJ Platform side (sandbox, JVM arguments) but wires up neither the
            // module's own test sources nor the platform test framework: without the lines below the task
            // first reports NO-SOURCE, and once the sources are in place it cannot load BasePlatformTestCase.
            //
            // The framework comes from `intellijPlatformTestClasspath` — the same configuration the `test`
            // task resolves. Deliberately NOT the per-task variant the registration above creates
            // (intellijPlatformTestClasspath_<name>): `testFramework(TestFrameworkType.Platform)` is declared
            // only for the default configuration, so the per-task one carries the IDE but no test-framework
            // jars, BasePlatformTestCase stays unresolvable and every platform test is silently skipped.
            // Reading the configuration rather than the `test` task also keeps `test` out of these runs.
            testClassesDirs = testSourceSet.output.classesDirs
            classpath = testSourceSet.runtimeClasspath +
                    project.configurations["intellijPlatformTestClasspath"] +
                    classpath

            filter {
                applyFilter()
                // A module may legitimately contain no test of the selected kind — most have no
                // integration tests at all.
                isFailOnNoMatchingTests = false
            }
        }
    }
}

registerTestSuite(
    "developerTest",
    "Runs the developer test suite: every test class NOT named *IT (the fast inner-loop suite)."
) { excludeTestsMatching("*IT") }

registerTestSuite(
    "integrationTest",
    "Runs the integration test suite: test classes named *IT."
) { includeTestsMatching("*IT") }

// `check` must keep running `test` and only `test` — otherwise the same tests run three times, concurrently,
// which besides tripling the build time can break
// wall-clock-based performance tests. Nothing wires the suites into `check` directly: it is Kover,
// whose report tasks aggregate EVERY Test task and which hangs `koverVerify` off `check`. The suites are
// therefore excluded from Kover instrumentation in each module's own build script, where Kover is applied.
