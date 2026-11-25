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

import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask

// :language — the ASN.1 language itself (lexer, parser, PSI, syntax highlighting and language related editor
// features). MUST NEVER depend on :plugin. Carries its own content module descriptor (asn-1.language.xml).
plugins {
    id("asn1.platform-module")
    alias(libs.plugins.grammarkit)
    id("org.jetbrains.dokka") version "2.2.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.11"
}

// Coverage is measured from `test`, which runs everything. Without this Kover's report tasks — and through
// `koverVerify` the whole `check`/`build` lifecycle — would additionally pull in developerTest and
// integrationTest, running every test three times concurrently. See asn1.platform-module.gradle.kts.
kover {
    currentProject {
        instrumentation {
            disabledForTestTasks.addAll("developerTest", "integrationTest")
        }
    }
    // Generated lexer, parser, element type holder and PSI implementations are not subject to coverage.
    reports {
        filters {
            excludes {
                classes(
                    "org.pcsoft.intellij.plugin.asn1.language.parser.AsnParser",
                    "org.pcsoft.intellij.plugin.asn1.language.parser._AsnLexer",
                    "org.pcsoft.intellij.plugin.asn1.language.parser.psi.AsnTypes*",
                    "org.pcsoft.intellij.plugin.asn1.language.parser.psi.impl.*",
                )
            }
        }
    }
}

dependencies {
    // Reads the object identifier registry (oid/oid-registry.yaml) into the Kotlin data classes.
    implementation(libs.jackson.yaml)
    implementation(libs.jackson.kotlin)
    testImplementation(libs.json.schema.validator)
}

val parsingRoot = "src/main/resources/parsing"
val parserPackage = "org/pcsoft/intellij/plugin/asn1/language/parser"

tasks {
    register<GenerateParserTask>("generateAsnParser") {
        group = "grammar-kit"
        description = "Generates the ASN.1 parser and PSI classes from AsnGrammar.bnf"
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/AsnGrammar.bnf"))
        targetRootOutputDir.set(layout.buildDirectory.dir("generated"))
        pathToParser.set("$parserPackage/AsnParser.java")
        pathToPsiRoot.set("$parserPackage/psi")
        purgeOldFiles.set(true)
    }

    register<GenerateLexerTask>("generateAsnLexer") {
        group = "grammar-kit"
        description = "Generates the ASN.1 lexer from AsnLexer.flex"
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/AsnLexer.flex"))
        targetOutputDir.set(layout.buildDirectory.dir("generated/$parserPackage"))
        // MUST stay false: the lexer shares its output directory with the parser task, whose PSI classes live in
        // the …/psi subdirectory. A purging lexer wipes that subdirectory recursively; with no ordering between
        // the two generate tasks this deletes the parser's PSI output whenever the lexer runs last.
        purgeOldFiles.set(false)
    }

    val generators = listOf("generateAsnLexer", "generateAsnParser")
    compileJava { dependsOn(generators) }
    compileKotlin { dependsOn(generators) }
}

sourceSets.main {
    java.srcDir(layout.buildDirectory.dir("generated"))
}
