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

import com.intellij.psi.PsiFileFactory
import com.intellij.psi.impl.DebugUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.asn1.language.AsnFileType

/**
 * Error tolerance tests (see `.claude/rules/parsing.md`): whatever text is in the editor while the user is
 * typing, parsing never fails and the tree always covers the whole text.
 */
class AsnParserRobustnessTest : BasePlatformTestCase() {
    private fun assertParsesCompletely(text: String) {
        val file = PsiFileFactory.getInstance(project).createFileFromText("test.asn", AsnFileType, text)
        assertEquals("Tree does not cover the text: '$text'", text, file.text)
        assertEquals(text.length, file.node.textLength)
        // Walking the whole tree must not fail either.
        assertNotNull(DebugUtil.psiToString(file, false, false))
    }

    /**
     * Use case: the user types a complete module header character by character.
     * Expected: every single prefix parses without exception and the tree covers exactly that prefix.
     */
    fun testEveryPrefixOfCompleteHeader() {
        val text = "My-Module { iso(1) identified-organization(3) dod(6) 1 } DEFINITIONS XER INSTRUCTIONS " +
                "AUTOMATIC TAGS EXTENSIBILITY IMPLIED ::= BEGIN Foo ::= INTEGER /* c /* n */ */ -- l\nEND"
        for (length in 0..text.length) {
            assertParsesCompletely(text.substring(0, length))
        }
    }

    /**
     * Use case: every suffix of the same header, which is what remains when the start is deleted.
     * Expected: no exception, the whole text is covered.
     */
    fun testEverySuffixOfCompleteHeader() {
        val text = "My-Module { iso(1) 3 } DEFINITIONS IMPLICIT TAGS ::= BEGIN END"
        for (start in 0..text.length) {
            assertParsesCompletely(text.substring(start))
        }
    }

    /**
     * Use case: unusual files: empty, only whitespace, only a comment, only braces, only keywords, text ending
     * inside a comment, binary-like characters.
     * Expected: no exception, the whole text is covered.
     */
    fun testUnusualFiles() {
        listOf(
            "", " ", "\n\n", "-- only a comment", "/* only a comment */", "/* open", "{", "}", "{ {", "( ) ) (",
            "DEFINITIONS", "BEGIN END", "::=", "::", ":", "END END END", "1 2 3", "a b c", "A", "A {",
            "A { 1", "A { 1 }", "A { iso(", "A { iso(1", "A DEFINITIONS XER", "A DEFINITIONS XER INSTRUCTIONS",
            "A DEFINITIONS ::= BEGIN /*", "\u0000\u0001§☃", "😀", "\r\n\t\u000B\u000C",
        ).forEach { assertParsesCompletely(it) }
    }

    /**
     * Use case: a module without `END` is followed by a second module.
     * Expected: parsing neither fails nor loses text.
     */
    fun testMissingEndBeforeNextModule() {
        assertParsesCompletely("A DEFINITIONS ::= BEGIN\nB DEFINITIONS ::= BEGIN END")
    }
}
