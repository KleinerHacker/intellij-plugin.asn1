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

package org.pcsoft.intellij.plugin.asn1.language.feature.editor

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.asn1.AsnIcons
import org.pcsoft.intellij.plugin.asn1.language.parser.AsnSyntaxHighlighter

/**
 * Tests for [AsnColorSettingsPage].
 */
class AsnColorSettingsPageTest : BasePlatformTestCase() {
    private val page = AsnColorSettingsPage()

    /**
     * Use case: the page is shown in the color settings dialog.
     * Expected: it has a name, the file icon and the ASN.1 highlighter.
     */
    fun testPageDescribesAsn1() {
        assertFalse(page.displayName.isBlank())
        assertSame(AsnIcons.FILE, page.icon)
        assertTrue(page.highlighter is AsnSyntaxHighlighter)
        assertEquals(0, page.colorDescriptors.size)
    }

    /**
     * Use case: the user wants to change a color of the ASN.1 language.
     * Expected: every highlighting key is listed with a readable name.
     */
    fun testEveryKeyIsListed() {
        val keys = page.attributeDescriptors.map { it.key }
        assertEquals(
            listOf(
                AsnSyntaxHighlighter.KEYWORD, AsnSyntaxHighlighter.ASSIGNMENT, AsnSyntaxHighlighter.NUMBER,
                AsnSyntaxHighlighter.LINE_COMMENT, AsnSyntaxHighlighter.BLOCK_COMMENT, AsnSyntaxHighlighter.BRACES,
                AsnSyntaxHighlighter.MODULE_NAME,
                AsnSyntaxHighlighter.OID_ARC_NAME, AsnSyntaxHighlighter.BAD_CHARACTER,
            ),
            keys,
        )
        assertTrue(page.attributeDescriptors.none { it.displayName.isBlank() })
    }

    /**
     * Use case: the preview of the color settings.
     * Expected: the demo text carries the tags for the colors set by the annotator and maps them to their keys.
     */
    fun testDemoTextAndTags() {
        val tags = page.additionalHighlightingTagToDescriptorMap
        assertSame(AsnSyntaxHighlighter.MODULE_NAME, tags["module"])
        assertSame(AsnSyntaxHighlighter.OID_ARC_NAME, tags["arc"])
        tags.keys.forEach { assertTrue(page.demoText.contains("<$it>")) }
    }
}
