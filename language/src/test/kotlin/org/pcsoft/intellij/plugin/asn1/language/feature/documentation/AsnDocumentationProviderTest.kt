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

package org.pcsoft.intellij.plugin.asn1.language.feature.documentation

import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for [AsnDocumentationProvider]: the quick documentation of the arcs of an object identifier.
 */
class AsnDocumentationProviderTest : BasePlatformTestCase() {
    private val provider = AsnDocumentationProvider()

    private fun doc(text: String): String? {
        myFixture.configureByText("test.asn", text)
        val leaf = myFixture.file.findElementAt(myFixture.caretOffset)
        val element = provider.getCustomDocumentationElement(myFixture.editor, myFixture.file, leaf, myFixture.caretOffset)
            ?: return null
        return provider.generateDoc(element, leaf)
    }

    private fun module(oid: String) = "Mod { $oid } DEFINITIONS ::= BEGIN END"

    /**
     * Use case: the caret stands on the name of an arc that is written with its number.
     * Expected: description, name, number and dotted notation of the arc are shown.
     */
    fun testNameOfNameAndNumberForm() {
        val doc = doc(module("iso(1) identified-organization(3) do<caret>d(6)"))
        assertNotNull(doc)
        assertTrue(doc!!, doc.contains("United States Department of Defense"))
        assertTrue(doc, doc.contains("iso(1) identified-organization(3) dod(6)"))
        assertTrue(doc, doc.contains("1.3.6"))
    }

    /**
     * Use case: the caret stands on the number between the parentheses of an arc.
     * Expected: the documentation of this arc.
     */
    fun testNumberInParentheses() {
        val doc = doc(module("iso(1) identified-organization(3) dod(<caret>6)"))
        assertTrue(doc.orEmpty(), doc.orEmpty().contains("United States Department of Defense"))
    }

    /**
     * Use case: arcs written as plain name and as plain number, mixed.
     * Expected: both are documented.
     */
    fun testPlainNameAndNumber() {
        assertTrue(doc(module("iso 3 d<caret>od")).orEmpty().contains("1.3.6"))
        assertTrue(doc(module("iso 3 <caret>6")).orEmpty().contains("1.3.6"))
        assertTrue(doc(module("<caret>iso")).orEmpty().contains("ISO"))
    }

    /**
     * Use case: the quick navigate info of an arc.
     * Expected: one line with name, number and dotted notation.
     */
    fun testQuickNavigateInfo() {
        myFixture.configureByText("test.asn", module("iso 3 d<caret>od"))
        val leaf = myFixture.file.findElementAt(myFixture.caretOffset)
        assertEquals("dod(6)  1.3.6", provider.getQuickNavigateInfo(leaf, leaf))
    }

    /**
     * Use case: the caret stands on something that is no known arc: an unknown arc, a path below an unknown arc,
     * the module name, a keyword, a defined value in the parentheses and a position outside the braces.
     * Expected: no documentation, no exception.
     */
    fun testNoDocumentation() {
        listOf(
            module("unk<caret>nown"),
            module("iso 99 <caret>1"),
            module("iso(1) member-body(<caret>99)"),
            "M<caret>od { iso } DEFINITIONS ::= BEGIN END",
            "Mod { iso } DEFIN<caret>ITIONS ::= BEGIN END",
            module("iso(va<caret>lue)"),
            "Mod DEFINITIONS ::= BEGIN <caret>1 END",
        ).forEach { text -> assertNull("Documented: $text", doc(text)) }
    }

    /**
     * Use case: the caret stands in incomplete text: braces never closed, an open parenthesis, nothing at all.
     * Expected: nothing fails.
     */
    fun testIncompleteText() {
        listOf("Mod { iso(1) mem<caret>", "Mod { iso(<caret>", "Mod { iso(1<caret>", "<caret>", "Mod { -- <caret>").forEach {
            doc(it)
        }
    }
}
