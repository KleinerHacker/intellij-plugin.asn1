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

// :language — the ASN.1 language itself (lexer, parser, PSI, syntax highlighting and language related editor
// features). MUST NEVER depend on :plugin. Carries its own content module descriptor (asn-1.language.xml).
plugins {
    id("asn1.platform-module")
    id("org.jetbrains.dokka") version "2.2.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.9"
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
}
