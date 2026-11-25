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

package org.pcsoft.intellij.plugin.asn1.language.parser

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.asn1.PluginBundle

/**
 * Tests for [AsnAnnotator]: colors of module name and object identifier arcs and the checks of the module header.
 */
class AsnAnnotatorTest : BasePlatformTestCase() {
    private fun colored(text: String): Map<String, TextAttributesKey> {
        myFixture.configureByText("test.asn", text)
        return myFixture.doHighlighting()
            .filter { it.forcedTextAttributesKey != null }
            .associate { myFixture.file.text.substring(it.startOffset, it.endOffset) to it.forcedTextAttributesKey }
    }

    private fun errors(text: String): List<String> {
        myFixture.configureByText("test.asn", text)
        return myFixture.doHighlighting()
            .filter { it.severity == HighlightSeverity.ERROR }
            .mapNotNull { it.description }
    }

    /**
     * Use case: the module name of a module definition.
     * Expected: it is colored as module name, other upper case words are not.
     */
    fun testModuleNameIsColored() {
        val colors = colored("My-Module DEFINITIONS XER INSTRUCTIONS ::= BEGIN Other ::= 1 END")
        assertSame(AsnSyntaxHighlighter.MODULE_NAME, colors["My-Module"])
        assertNull(colors["XER"])
        assertNull(colors["Other"])
    }

    /**
     * Use case: the object identifier `{ iso(1) identified-organization(3) dod(6) }` of a module identifier.
     * Expected: all names get the same arc name color, the root arc `iso` as well as the other names.
     */
    fun testObjectIdentifierArcsAreColored() {
        val colors = colored("M { iso(1) identified-organization(3) dod(6) } DEFINITIONS ::= BEGIN END")
        assertSame(AsnSyntaxHighlighter.OID_ARC_NAME, colors["iso"])
        assertSame(AsnSyntaxHighlighter.OID_ARC_NAME, colors["identified-organization"])
        assertSame(AsnSyntaxHighlighter.OID_ARC_NAME, colors["dod"])
    }

    /**
     * Use case: the three root arc names in name-only form and with number, and at any position.
     * Expected: the position and the root arc do not matter, every arc name has the same color.
     */
    fun testAllArcNamesHaveTheSameColor() {
        assertSame(
            AsnSyntaxHighlighter.OID_ARC_NAME,
            colored("M { joint-iso-itu-t 5 } DEFINITIONS ::= BEGIN END")["joint-iso-itu-t"],
        )
        assertSame(
            AsnSyntaxHighlighter.OID_ARC_NAME,
            colored("M { itu-t(0) 5 } DEFINITIONS ::= BEGIN END")["itu-t"],
        )
        assertSame(
            AsnSyntaxHighlighter.OID_ARC_NAME,
            colored("M { 1 iso } DEFINITIONS ::= BEGIN END")["iso"],
        )
    }

    /**
     * Use case: incomplete object identifiers while typing.
     * Expected: the typed names are colored, nothing fails.
     */
    fun testIncompleteObjectIdentifier() {
        assertSame(AsnSyntaxHighlighter.OID_ARC_NAME, colored("M { iso(")["iso"])
        assertSame(AsnSyntaxHighlighter.MODULE_NAME, colored("M { iso(1")["M"])
        // Without a module the braces belong to no object identifier: no arc color, only the stray token error.
        assertTrue(colored("{ iso").values.none { it === AsnSyntaxHighlighter.OID_ARC_NAME })
    }

    /**
     * Use case: a number with a leading zero.
     * Expected: an error is reported; a plain `0` and numbers without leading zero are fine.
     */
    fun testLeadingZeroNumber() {
        assertEquals(
            listOf(PluginBundle.message("annotator.number.leading-zero")),
            errors("M { 1 007 } DEFINITIONS ::= BEGIN END").filter { it == PluginBundle.message("annotator.number.leading-zero") },
        )
        assertTrue(errors("M { 0 10 } DEFINITIONS ::= BEGIN END").isEmpty())
    }

    /**
     * Use case: module defaults in the wrong order.
     * Expected: the default that comes too late is reported as wrong order.
     */
    fun testWrongOrderOfDefaults() {
        val message = PluginBundle.message("annotator.module.default.order")
        assertTrue(errors("M DEFINITIONS EXTENSIBILITY IMPLIED AUTOMATIC TAGS ::= BEGIN END").contains(message))
        assertTrue(errors("M DEFINITIONS AUTOMATIC TAGS XER INSTRUCTIONS ::= BEGIN END").contains(message))
        assertFalse(
            errors("M DEFINITIONS XER INSTRUCTIONS AUTOMATIC TAGS EXTENSIBILITY IMPLIED ::= BEGIN END").contains(message),
        )
    }

    /**
     * Use case: the same module default is written twice.
     * Expected: the second one is reported as duplicate.
     */
    fun testDuplicateDefaults() {
        val message = PluginBundle.message("annotator.module.default.duplicate")
        assertTrue(errors("M DEFINITIONS AUTOMATIC TAGS IMPLICIT TAGS ::= BEGIN END").contains(message))
        assertFalse(errors("M DEFINITIONS AUTOMATIC TAGS ::= BEGIN END").contains(message))
    }

    /**
     * Use case: a single stray token in front of a module and between two modules.
     * Expected: it is reported as unexpected token, the modules themselves are not.
     */
    fun testStrayTokenIsReported() {
        val message = PluginBundle.message("annotator.unexpected-token")
        assertEquals(listOf(message), errors("1 M DEFINITIONS ::= BEGIN END"))
        assertEquals(listOf(message), errors("A DEFINITIONS ::= BEGIN END ) B DEFINITIONS ::= BEGIN END"))
        assertFalse(errors("A DEFINITIONS ::= BEGIN END B DEFINITIONS ::= BEGIN END").contains(message))
    }

    /**
     * Use case: stray text in the module body that belongs to no assignment.
     * Expected: it is reported as error; a body with valid assignments only has no such error.
     */
    fun testJunkInBodyIsReported() {
        val message = PluginBundle.message("annotator.assignment.junk")
        assertTrue(errors("M DEFINITIONS ::= BEGIN ) A ::= INTEGER END").contains(message))
        assertFalse(errors("M DEFINITIONS ::= BEGIN A ::= INTEGER b A ::= 1 END").contains(message))
    }

    /**
     * Use case: an assignment without right hand side while typing (`A ::=`, `x INTEGER ::=`).
     * Expected: the missing right hand side is reported; complete assignments are not.
     */
    fun testMissingNotationIsReported() {
        val message = PluginBundle.message("annotator.assignment.missing-notation")
        assertTrue(errors("M DEFINITIONS ::= BEGIN A ::= END").contains(message))
        assertTrue(errors("M DEFINITIONS ::= BEGIN x INTEGER ::= END").contains(message))
        assertFalse(errors("M DEFINITIONS ::= BEGIN A ::= INTEGER x INTEGER ::= 1 END").contains(message))
    }

    /**
     * Use case: a file with nothing but incomplete text.
     * Expected: the annotator does not fail on any of these inputs.
     */
    fun testIncompleteTextDoesNotFail() {
        listOf("", "M", "M DEFINITIONS", "M DEFINITIONS EXPLICIT", "{", "( ) }", "/* open").forEach {
            myFixture.configureByText("test.asn", it)
            assertNotNull(myFixture.doHighlighting())
        }
    }
}
