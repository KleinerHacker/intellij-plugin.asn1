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


import com.github.jk1.license.render.ReportRenderer
import org.gradle.api.tasks.testing.TestFilter
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.tasks.SignPluginTask
import java.time.Duration

// :plugin — the publishable IntelliJ plugin. Contains ONLY IDE specific extensions apart from the language
// itself (settings, build / run configurations, tool windows) and embeds the :language module.
//
// Plugins applied WITHOUT a version are already on the build classpath via buildSrc (kotlin.jvm, intellij
// platform); the rest are versioned here.
plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog") version "2.5.0"
    id("org.jetbrains.dokka") version "2.2.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.11"
    id("com.github.jk1.dependency-license-report") version "3.1.4"
    id("org.cyclonedx.bom") version "3.4.1"
    id("app.cash.licensee") version "1.14.1"
}

kotlin {
    // See the convention plugin (asn1.platform-module): compile on JDK 25 for the Java 25 platform
    // jars of IntelliJ 2026.2, but emit Java 21 bytecode for load compatibility across the supported range.
    jvmToolchain(25)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        // Do not emit delegating overrides for the default methods of implemented Java interfaces, so that
        // verifyPlugin does not report deprecated default methods that the source never overrides.
        // Applied to this module only: :language is published as a Maven artifact, where dropping the
        // DefaultImpls classes would be a binary-compatibility change for consumers.
        freeCompilerArgs.add("-jvm-default=no-compatibility")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

intellijPlatform {
    instrumentCode = false

    pluginConfiguration {
        ideaVersion {
            sinceBuild = "263"
            untilBuild = provider { null }   // unbounded: covers 2026.3 (263) and future IDEs
        }
        // Release-time changelog injection is wired separately; keep patchPluginXml off the changelog
        // provider so it stays configuration-cache friendly.
        changeNotes = provider { "" }
    }

    // The plugin declares sinceBuild=263 with an unbounded untilBuild, so it is offered to *every* 263+
    // IDE — not just IntelliJ IDEA. Verifying only the development IDE would let a break in a sibling
    // product surface in the marketplace instead of in the build. These four cover the IDEs the plugin is
    // meant to support; the version is taken from the catalog so it can never drift from the platform the
    // plugin is compiled against. See .claude/rules/plugin-verification.md — the list MUST be kept current.
    pluginVerification {
        ides {
            val verificationVersion = libs.versions.idea.get()
            listOf(
                IntelliJPlatformType.IntellijIdea,
                IntelliJPlatformType.Rider,
                IntelliJPlatformType.CLion,
                IntelliJPlatformType.GoLand,
                // The IDEs are resolved from their archives instead of their installers: the IntelliJ Platform
                // Gradle Plugin does not support Rider as a target platform with `useInstaller = true` and
                // warns about it on every verification run. The setting is applied to the whole matrix so all
                // four IDEs are obtained the same way.
            ).forEach { create(it, verificationVersion) { useInstaller = false } }
        }
    }

    signing {
        System.getenv("KEYSTORE_FILE")?.takeIf { it.isNotBlank() }?.let { keyStore = file(it) }
        keyStoreType = "PKCS12"
        System.getenv("KEY_ALIAS")?.takeIf { it.isNotBlank() }?.let { keyStoreKeyAlias = it }

        val pwFile = System.getenv("KEYSTORE_PASSWORD_FILE")?.takeIf { it.isNotBlank() }
        keyStorePassword = if (pwFile != null) {
            providers.fileContents(layout.file(provider { File(pwFile) })).asText.map { it.trim() }
        } else {
            providers.environmentVariable("KEYSTORE_PASSWORD")
        }
    }

    publishing {
        val tokenFile = System.getenv("PUBLISH_TOKEN_FILE")?.takeIf { it.isNotBlank() }
        token = if (tokenFile != null) {
            providers.fileContents(layout.file(provider { File(tokenFile) })).asText.map { it.trim() }
        } else {
            providers.environmentVariable("PUBLISH_TOKEN")
        }
        channels = listOf(providers.gradleProperty("publishChannel").getOrElse("default"))
    }
}

// Resolved once here because both the `intellijPlatform` dependencies and the custom test suites at the
// bottom of this file need it. See the convention plugin (asn1.platform-module) for the rationale.
val localIdePath: String? = (providers.gradleProperty("localIdePath").orNull
    ?: providers.environmentVariable("LOCAL_IDE_PATH").orNull)?.takeIf { it.isNotBlank() }

dependencies {
    // See asn1.platform-module: pin transitive Kotlin artifacts to the catalog version so an older
    // stdlib (pulled via jackson-module-kotlin → kotlin-reflect) cannot crash the platform's coroutine
    // debug probes with a "Debug metadata version mismatch" and hang the tests.
    constraints {
        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlin.reflect)
    }

    testImplementation("junit:junit:4.13.2")

    intellijPlatform {
        if (localIdePath != null) {
            local(localIdePath)
        } else {
            intellijIdea(libs.versions.idea.get())
        }
        testFramework(TestFrameworkType.Platform)
    }

    // Bundle the language module as a regular library into the plugin (one shared classloader). It ships
    // its own content module descriptor (asn-1.language.xml) that the main plugin.xml declares via <content>.
    implementation(project(":language"))
}

kover {
    // Coverage is measured from `test`, which runs everything. Without this Kover's report tasks — and
    // through `koverVerify` the whole `check`/`build` lifecycle — would additionally pull in developerTest
    // and integrationTest, running every test three times concurrently.
    currentProject {
        instrumentation {
            disabledForTestTasks.addAll("developerTest", "integrationTest")
        }
    }
}

licenseReport {
    outputDir = layout.buildDirectory.dir("licences").get().asFile.absolutePath
    configurations = arrayOf("runtimeClasspath")
    renderers = arrayOf<ReportRenderer>(
        com.github.jk1.license.render.JsonReportRenderer(),
        com.github.jk1.license.render.SimpleHtmlReportRenderer()
    )
}

plugins.withId("org.jetbrains.kotlin.jvm") {
    plugins.withId("app.cash.licensee") {
        extensions.configure<app.cash.licensee.LicenseeExtension> {
            listOf("Apache-2.0").forEach(::allow)
        }
    }
}

tasks {
    register<SignPluginTask>("selfSignPlugin") {
        val signPluginTask = named<SignPluginTask>("signPlugin")
        group = "intellij platform"
        description = "Sign the plugin locally with the project's own PKCS#12 keystore (.signing/)."

        archiveFile.set(signPluginTask.flatMap { it.archiveFile })
        signedArchiveFile.set(signPluginTask.flatMap { it.signedArchiveFile })
        zipSignerExecutable.set(signPluginTask.flatMap { it.zipSignerExecutable })

        keyStore.set(layout.projectDirectory.file(".signing/keystore.p12"))
        keyStoreType.set("PKCS12")
        keyStoreKeyAlias.set(
            providers.gradleProperty("keyAlias")
                .orElse(providers.environmentVariable("KEY_ALIAS"))
                .orElse("asn-1")
        )
        keyStorePassword.set(
            providers.gradleProperty("keyPassword")
                .orElse(providers.environmentVariable("KEYSTORE_PASSWORD"))
        )
    }

    // Applies to `test` as well as to the developerTest/integrationTest suites registered below — those are
    // TestIdeTask instances, which extend Test.
    withType<Test>().configureEach {
        // Platform tests log through java.util.logging (JUL) via TestLoggerFactory — NOT log4j. See the
        // detailed explanation in the :language convention (asn1.platform-module.gradle.kts).
        systemProperty("intellij.console.log.level", "off")
        systemProperty("idea.log.config.file", "${rootDir}/gradle/test-logging.properties")
        systemProperty("idea.split.test.logs", "true")
        timeout.set(Duration.ofMinutes(15))
    }
}

// ─────────────────────────────────────────────────────────────────────────────────────────────
// The two test suites — see the root build.gradle.kts for the convention, and the identical registration in
// asn1.platform-module.gradle.kts for the language modules. `test` keeps running everything (that is
// what `check` depends on); these two are the filtered entry points.
//
// Registered through `intellijPlatformTesting.testIde` rather than as plain Test tasks: the platform plugin
// does not configure `test` in place but supplies it through a dedicated `prepareTest` task, so a hand-rolled
// Test task gets no sandbox and no platform system properties.
// ─────────────────────────────────────────────────────────────────────────────────────────────
// Both resolved against the project — inside the `task { }` block below the receiver is the task itself.
val testSourceSet = sourceSets[SourceSet.TEST_SOURCE_SET_NAME]

fun registerTestSuite(name: String, taskDescription: String, applyFilter: TestFilter.() -> Unit) {
    intellijPlatformTesting.testIde.register(name) {
        if (localIdePath != null) {
            localPath.set(file(localIdePath))
        } else {
            type.set(IntelliJPlatformType.IntellijIdea)
            version.set(libs.versions.idea.get())
        }

        task {
            group = LifecycleBasePlugin.VERIFICATION_GROUP
            description = taskDescription

            // `testIde` supplies the IntelliJ Platform side (sandbox, JVM arguments) but wires up neither the
            // module's own test sources nor the platform test framework — see the identical block in
            // asn1.platform-module.gradle.kts for the full reasoning.
            testClassesDirs = testSourceSet.output.classesDirs
            classpath = testSourceSet.runtimeClasspath +
                    project.configurations["intellijPlatformTestClasspath"] +
                    classpath

            filter {
                applyFilter()
                // A module may legitimately contain no test of the selected kind.
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
