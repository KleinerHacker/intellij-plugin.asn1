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

package org.pcsoft.intellij.plugin.asn1.language

import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for [AsnLanguage] and [AsnFileType].
 */
class AsnFileTypeTest : BasePlatformTestCase() {
    /**
     * Use case: a file named `*.asn` or `*.asn1` is opened in the IDE.
     * Expected: the file type registry resolves it to [AsnFileType].
     */
    fun testExtensionsAreRegistered() {
        val manager = FileTypeManager.getInstance()
        assertSame(AsnFileType, manager.getFileTypeByFileName("module.asn"))
        assertSame(AsnFileType, manager.getFileTypeByFileName("module.asn1"))
    }

    /**
     * Use case: the platform asks the file type for its language, name and extension.
     * Expected: the values describe the ASN.1 language.
     */
    fun testFileTypeDescribesAsnLanguage() {
        assertSame(AsnLanguage, AsnFileType.language)
        assertEquals("ASN.1", AsnFileType.name)
        assertEquals("asn", AsnFileType.defaultExtension)
        assertFalse(AsnFileType.description.isBlank())
    }
}
