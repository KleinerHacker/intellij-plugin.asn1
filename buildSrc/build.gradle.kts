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

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

// Plugin artifacts needed so the precompiled convention scripts can apply these plugins by id.
// Versions mirror gradle/libs.versions.toml (buildSrc cannot consume the root version catalog by default).
dependencies {
    implementation("org.jetbrains.intellij.platform:intellij-platform-gradle-plugin:2.18.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.10")
}
