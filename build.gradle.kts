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


// ─────────────────────────────────────────────────────────────────────────────────────────────
// Root project: pure aggregator. It contains NO production code and NO plugin descriptor.
//   • The publishable plugin lives in        :plugin
//   • The ASN.1 language (lexer/parser/PSI/editor features) in   :language
//
// The root only aggregates cross-module concerns: Dokka (over all modules), coverage merge, the docs
// site (MkDocs/mike) and a convenience umbrella for source generation.
// ─────────────────────────────────────────────────────────────────────────────────────────────

plugins {
    id("org.jetbrains.dokka") version "2.3.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.11"
}

// Dokka MUST aggregate over all child modules from the root — otherwise the generated API docs would
// only cover whichever single module applied Dokka. Every module applies Dokka; the root pulls them in.
dependencies {
    dokka(project(":language"))
    dokka(project(":plugin"))

    // Merge coverage across all modules into one report.
    kover(project(":language"))
    kover(project(":plugin"))
}

tasks {
    //region Dokka
    register<Copy>("copyDokka") {
        group = "dokka"
        description = "Copy aggregated Dokka to MkDocs"
        from(File("build/dokka"))
        into(File("docs/docs/dokka"))
        dependsOn("dokkaGeneratePublicationHtml")
    }

    register<Delete>("deleteDokka") {
        group = "dokka"
        description = "Delete Dokka"
        delete(File("docs/docs/dokka"))
    }
    //endregion

    //region Licencing
    register<Copy>("copyLicenceReport") {
        group = "licencing"
        description = "Copy licence report to MkDocs"
        from(File("plugin/build/licences"))
        into(File("docs/docs/licences"))
        dependsOn(":plugin:generateLicenseReport")
    }

    register<Delete>("deleteLicenceReport") {
        group = "licencing"
        description = "Delete licence report"
        delete(File("docs/docs/licences"))
    }
    //endregion

    //region MkDocs
    // mike spawns `mkdocs` as a subprocess; on Windows the Python Scripts dir
    // (where mkdocs.exe lives) is often not on PATH. Resolve it once and prepend
    // it to PATH for the mike tasks. In CI (setup-python) it is already on PATH.
    val pythonScriptsDir: String? by lazy {
        runCatching {
            providers.exec {
                commandLine("python", "-c", "import sysconfig; print(sysconfig.get_path('scripts'))")
            }.standardOutput.asText.get().trim().ifEmpty { null }
        }.getOrNull()
    }

    fun Exec.withMikePath() {
        pythonScriptsDir?.let { dir ->
            environment("PATH", dir + File.pathSeparator + System.getenv("PATH"))
        }
    }

    register<Exec>("installMkDocs") {
        group = null
        description = "Install mkdocs"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "--upgrade", "mkdocs")
    }

    register<Exec>("installMkDocsMaterial") {
        group = null
        description = "Install mkdocs-material"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "--upgrade", "mkdocs-material")
    }

    register<Exec>("installGitHubPages") {
        group = null
        description = "Install ghp-import"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "--upgrade", "ghp-import")
    }

    register<Exec>("installMike") {
        group = null
        description = "Install mike for versioned docs deployment"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "--upgrade", "mike")
    }

    register<Exec>("installI18N") {
        group = null
        description = "Install i18n"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "--upgrade", "mkdocs-static-i18n")
    }

    register("installDocs") {
        group = "MKDocs"
        description = "Install mkdocs and dependencies"
        dependsOn("installMkDocs")
        dependsOn("installMkDocsMaterial")
        dependsOn("installGitHubPages")
        dependsOn("installI18N")
        dependsOn("installMike")
    }

    register<Exec>("runDocs") {
        group = "MKDocs"
        description = "Run mkdocs serve and open browser (no version selector — that only appears on the deployed site)"
        workingDir = file("docs")
        commandLine("python", "-m", "mkdocs", "serve", "-o", "-w", ".", "-w", "./docs")
        dependsOn("installDocs", "copyDokka", "copyLicenceReport")
        finalizedBy("deleteDokka", "deleteLicenceReport")
    }

    register<Exec>("buildDocs") {
        group = "MKDocs"
        description =
            "Build the mkdocs site into build/docs (per mkdocs.yml site_dir; no serve, no deploy) — usable as a generation test"
        workingDir = file("docs")
        // --strict fails the build on warnings (broken links, missing pages …) so it acts as a test;
        // --clean wipes the previous output first.
        commandLine("python", "-m", "mkdocs", "build", "--clean", "--strict")
        dependsOn("installDocs", "copyDokka", "copyLicenceReport")
        finalizedBy("deleteDokka", "deleteLicenceReport")
    }

    register<Exec>("deployDocs") {
        group = "MKDocs"
        description =
            "Deploy a versioned docs snapshot via mike. Requires -Pversion=<tag> and a pre-configured git push target."
        workingDir = file("docs")
        val ver = (project.findProperty("version") as String?)
            ?: error("Pass -Pversion=<tag> to deployDocs")
        val setLatest = (project.findProperty("setLatest") as String?) != "false"
        val args = buildList {
            add("python"); add("-c"); add("from mike.driver import main; main()"); add("deploy"); add("--push")
            // Materialise the 'latest' alias as a full copy, not mike's default symlink:
            // GitHub Pages does not resolve git symlinks reliably, and the gh-pages root
            // redirect points at latest/, so it must be a real directory.
            if (setLatest) {
                add("--alias-type"); add("copy"); add("--update-aliases"); add(ver); add("latest")
            } else add(ver)
        }
        commandLine(args)
        withMikePath()
        dependsOn("installDocs", "copyDokka", "copyLicenceReport")
        finalizedBy("deleteDokka", "deleteLicenceReport")
    }

    register<Exec>("setDefaultDocs") {
        group = "MKDocs"
        description =
            "Set the default docs version shown at the root URL via mike (run once after the first release deploy)."
        workingDir = file("docs")
        commandLine("python", "-c", "from mike.driver import main; main()", "set-default", "--push", "latest")
        withMikePath()
        dependsOn("installDocs")
    }
    //endregion
}

// ─────────────────────────────────────────────────────────────────────────────────────────────
// Developer tests vs. integration tests
//
// Project-wide convention: a test class whose name ends in `IT` is an *integration test* — it exercises a
// shipped artifact or the interplay of several layers, and may measure time. Everything else is a
// *developer test* and must stay fast, because it is the inner feedback loop.
//
//     ./gradlew test              → everything (what `check` runs)
//     ./gradlew developerTest     → every class NOT named *IT
//     ./gradlew integrationTest   → only classes named *IT
//
// The two suites are registered per module — see `asn1.platform-module.gradle.kts` for the language
// module and `plugin/build.gradle.kts` for :plugin — because each suite needs the IntelliJ Platform and
// sandbox configuration that only that module knows about. They deliberately are NOT plain `Test` tasks
// registered from here: the platform plugin does not mutate `test` directly but feeds it through a dedicated
// `prepareTest` task, so a hand-rolled Test task boots an unconfigured IDE (every test then dies with
// "Must be precomputed" out of JBUIScale) and additionally breaks the configuration cache.
// ─────────────────────────────────────────────────────────────────────────────────────────────
