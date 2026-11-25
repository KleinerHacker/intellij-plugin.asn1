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
 * Error tolerance tests of the assignment grammar (see `.claude/rules/parsing.md`).
 */
class AsnAssignmentRobustnessTest : BasePlatformTestCase() {
    private fun assertParsesCompletely(text: String) {
        val file = PsiFileFactory.getInstance(project).createFileFromText("test.asn", AsnFileType, text)
        assertEquals("Tree does not cover the text: '$text'", text, file.text)
        assertNotNull(DebugUtil.psiToString(file, false, false))
    }

    private val module = "M DEFINITIONS AUTOMATIC TAGS ::= BEGIN\n" +
            "Foo ::= SEQUENCE { a INTEGER (1..max), b [0] BOOLEAN } -- c\n" +
            "bar Foo ::= { a 1, b TRUE } /* x /* n */ */\n" +
            "x [1] INTEGER ::= 5\nEND"

    /**
     * Use case: the user types a complete module with assignments character by character.
     * Expected: every prefix parses without exception and the tree covers the whole prefix.
     */
    fun testEveryPrefix() {
        for (length in 0..module.length) assertParsesCompletely(module.substring(0, length))
    }

    /**
     * Use case: the start of the module is deleted piece by piece.
     * Expected: every suffix parses without exception and the tree covers the whole suffix.
     */
    fun testEverySuffix() {
        for (start in 0..module.length) assertParsesCompletely(module.substring(start))
    }

    /**
     * Use case: unusual bodies: empty file, comment only, opening brace only, text ending inside a comment,
     * incomplete assignments.
     * Expected: no exception, the whole text is covered.
     */
    fun testUnusualBodies() {
        listOf(
            "", "-- only a comment", "{", "M DEFINITIONS ::= BEGIN {", "M DEFINITIONS ::= BEGIN /* open",
            "M DEFINITIONS ::= BEGIN Name ::=", "M DEFINITIONS ::= BEGIN Name ::= END",
            "M DEFINITIONS ::= BEGIN x ::=", "M DEFINITIONS ::= BEGIN x [ ::= 1 END",
            "M DEFINITIONS ::= BEGIN ::= ::= ::= END", "M DEFINITIONS ::= BEGIN } } END",
            "M DEFINITIONS ::= BEGIN a b c d e f g END", "M DEFINITIONS ::= BEGIN A ::= { { { END",
        ).forEach { assertParsesCompletely(it) }
    }
}
