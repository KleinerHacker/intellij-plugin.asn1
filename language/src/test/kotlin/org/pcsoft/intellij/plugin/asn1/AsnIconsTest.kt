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

package org.pcsoft.intellij.plugin.asn1

import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for [AsnIcons].
 */
class AsnIconsTest : BasePlatformTestCase() {
    /**
     * Use case: the file icon is requested by the platform for every ASN.1 file.
     * Expected: the icon is loaded from the resources and has a positive size.
     */
    fun testFileIconIsLoaded() {
        assertTrue(AsnIcons.FILE.iconWidth > 0)
        assertTrue(AsnIcons.FILE.iconHeight > 0)
    }
}
