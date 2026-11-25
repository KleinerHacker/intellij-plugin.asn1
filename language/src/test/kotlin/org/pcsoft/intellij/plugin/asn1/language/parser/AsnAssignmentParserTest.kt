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

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.asn1.language.AsnFileType
import org.pcsoft.intellij.plugin.asn1.language.parser.psi.AsnTypes

/**
 * Tests for the assignment skeleton of the module body: type assignments, value assignments and junk.
 */
class AsnAssignmentParserTest : BasePlatformTestCase() {
    private fun parse(body: String): PsiElement =
        PsiFileFactory.getInstance(project).createFileFromText("test.asn", AsnFileType, "M DEFINITIONS ::= BEGIN $body END")

    private fun names(file: PsiElement, type: com.intellij.psi.tree.IElementType): List<String> =
        PsiTreeUtil.collectElements(file) { it.node.elementType == type }.map { it.firstChild.text }

    private fun errorCount(file: PsiElement): Int = PsiTreeUtil.collectElements(file) { it is PsiErrorElement }.size

    /**
     * Use case: one type assignment in the module body.
     * Expected: one type assignment node, no value assignment, no junk, no parse error.
     */
    fun testTypeAssignment() {
        val file = parse("Foo ::= INTEGER")
        assertEquals(listOf("Foo"), names(file, AsnTypes.TYPE_ASSIGNMENT))
        assertTrue(names(file, AsnTypes.VALUE_ASSIGNMENT).isEmpty())
        assertTrue(names(file, AsnTypes.ASSIGNMENT_JUNK).isEmpty())
        assertEquals(0, errorCount(file))
    }

    /**
     * Use case: one value assignment whose type looks like a type assignment start (`INTEGER ::=`).
     * Expected: one value assignment node, the type is not mistaken for a new assignment.
     */
    fun testValueAssignment() {
        val file = parse("answer INTEGER ::= 42")
        assertEquals(listOf("answer"), names(file, AsnTypes.VALUE_ASSIGNMENT))
        assertTrue(names(file, AsnTypes.TYPE_ASSIGNMENT).isEmpty())
        assertEquals(0, errorCount(file))
    }

    /**
     * Use case: several assignments of both kinds one after another, with braces and comments in between.
     * Expected: every assignment is recognized separately, none swallows the next one.
     */
    fun testMultipleAssignmentsWithComments() {
        val file = parse(
            "A ::= SEQUENCE { a INTEGER, b BOOLEAN } -- c\n" +
                    "b INTEGER ::= 1\n/* x */ C ::= CHOICE { x A }\nd A ::= { a 1, b TRUE }",
        )
        assertEquals(listOf("A", "C"), names(file, AsnTypes.TYPE_ASSIGNMENT))
        assertEquals(listOf("b", "d"), names(file, AsnTypes.VALUE_ASSIGNMENT))
        assertEquals(0, errorCount(file))
    }

    /**
     * Use case: a value whose last word is an identifier, directly followed by another value assignment.
     * Expected: the identifier belongs to the first value, the second assignment starts afterwards.
     */
    fun testIdentifierValueDoesNotSwallowNextAssignment() {
        val file = parse("a INTEGER ::= b\nc INTEGER ::= 2")
        assertEquals(listOf("a", "c"), names(file, AsnTypes.VALUE_ASSIGNMENT))
    }

    /**
     * Use case: a tagged type as value type (`x [0] INTEGER ::= 1`).
     * Expected: recognized as value assignment.
     */
    fun testValueAssignmentWithTaggedType() {
        val file = parse("x [0] INTEGER ::= 1")
        assertEquals(listOf("x"), names(file, AsnTypes.VALUE_ASSIGNMENT))
    }

    /**
     * Use case: a type assignment is missing its notation and is followed by a valid assignment.
     * Expected: the first assignment has no notation (reported by the annotator), the following one stays intact.
     */
    fun testMissingNotationKeepsNextAssignment() {
        val file = parse("A ::=\nB ::= INTEGER")
        assertEquals(listOf("A", "B"), names(file, AsnTypes.TYPE_ASSIGNMENT))
        assertEquals(listOf("INTEGER"), names(file, AsnTypes.TYPE_NOTATION))
    }

    /**
     * Use case: an assignment lacks its `::=`, a valid assignment follows.
     * Expected: the text before is junk, the following assignment stays intact.
     */
    fun testMissingAssignSymbolKeepsNextAssignment() {
        val file = parse("A INTEGER\nB ::= INTEGER")
        assertEquals(listOf("B"), names(file, AsnTypes.TYPE_ASSIGNMENT))
        assertTrue(names(file, AsnTypes.ASSIGNMENT_JUNK).isNotEmpty())
    }

    /**
     * Use case: an unterminated brace group is followed by a valid assignment.
     * Expected: the group ends before the next assignment, which is still found.
     */
    fun testUnterminatedBraceKeepsNextAssignment() {
        val file = parse("A ::= SEQUENCE {\nB ::= INTEGER")
        assertEquals(listOf("A", "B"), names(file, AsnTypes.TYPE_ASSIGNMENT))
        assertTrue(errorCount(file) > 0)
    }

    /**
     * Use case: stray tokens in the module body.
     * Expected: they become assignment junk; a following assignment is parsed normally.
     */
    fun testJunkInBody() {
        val file = parse("1 2 ) A ::= INTEGER")
        assertTrue(names(file, AsnTypes.ASSIGNMENT_JUNK).isNotEmpty())
        assertEquals(listOf("A"), names(file, AsnTypes.TYPE_ASSIGNMENT))
    }
}
