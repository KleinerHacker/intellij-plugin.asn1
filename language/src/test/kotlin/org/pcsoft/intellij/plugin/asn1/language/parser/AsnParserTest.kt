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

import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.asn1.language.AsnFileType
import org.pcsoft.intellij.plugin.asn1.language.parser.psi.AsnTypes

/**
 * Tests for the ASN.1 parser: every variant of the module definition header and the recovery behaviour on broken
 * input. The object identifier value as a rule of its own is tested in [AsnObjectIdentifierValueTest].
 */
class AsnParserTest : BasePlatformTestCase() {
    private fun parse(text: String): PsiFile =
        PsiFileFactory.getInstance(project).createFileFromText("test.asn", AsnFileType, text)

    private fun count(file: PsiFile, type: IElementType): Int =
        PsiTreeUtil.collectElements(file) { it.node?.elementType == type }.size

    private fun assertValid(text: String) {
        val file = parse(text)
        assertEquals(text, file.text)
        assertFalse("Unexpected error in: $text", PsiTreeUtil.hasErrorElements(file))
        assertEquals(1, count(file, AsnTypes.MODULE_DEFINITION))
    }

    private fun assertBroken(text: String) {
        val file = parse(text)
        assertEquals(text, file.text)
        assertTrue("Error expected in: $text", PsiTreeUtil.hasErrorElements(file))
    }

    /** All orderings of all subsets of [items], including the empty subset. */
    private fun <T> arrangements(items: List<T>): List<List<T>> {
        fun permutations(list: List<T>): List<List<T>> =
            if (list.size <= 1) listOf(list)
            else list.indices.flatMap { i -> permutations(list.filterIndexed { j, _ -> j != i }).map { listOf(list[i]) + it } }

        return (0 until (1 shl items.size)).flatMap { mask ->
            permutations(items.filterIndexed { index, _ -> mask and (1 shl index) != 0 })
        }
    }

    /**
     * Use case: the minimal module header without object identifier and without any default.
     * Expected: one module definition with identifier, no definitive identifier, no error.
     */
    fun testModuleWithoutObjectIdentifier() {
        assertValid("MyModule DEFINITIONS ::= BEGIN END")
        val file = parse("MyModule DEFINITIONS ::= BEGIN END")
        assertEquals(1, count(file, AsnTypes.MODULE_IDENTIFIER))
        assertEquals(0, count(file, AsnTypes.DEFINITIVE_IDENTIFIER))
    }

    /**
     * Use case: all forms of definitive object identifier components in a module header: numbers only,
     * name and number, name only and a mixture of them.
     * Expected: no error and the matching component structures in the tree.
     */
    fun testDefinitiveIdentifierVariants() {
        assertValid("M { 1 3 6 1 4 1 99 } DEFINITIONS ::= BEGIN END")
        assertValid("M { iso(1) identified-organization(3) dod(6) internet(1) } DEFINITIONS ::= BEGIN END")
        assertValid("M { iso standard 8571 } DEFINITIONS ::= BEGIN END")
        assertValid("M { iso(1) 3 6 internet(1) private(4) 1 } DEFINITIONS ::= BEGIN END")

        val file = parse("M { iso(1) 3 internet } DEFINITIONS ::= BEGIN END")
        assertEquals(1, count(file, AsnTypes.DEFINITIVE_IDENTIFIER))
        assertEquals(3, count(file, AsnTypes.DEFINITIVE_OBJ_ID_COMPONENT))
        assertEquals(1, count(file, AsnTypes.DEFINITIVE_NAME_AND_NUMBER_FORM))
        assertEquals(2, count(file, AsnTypes.NAME_FORM))
    }

    /**
     * Use case: the three root arcs of the object identifier tree in the module header, as name only and as
     * name with number.
     * Expected: no error, the root arc is an ordinary component for the parser.
     */
    fun testDefinitiveIdentifierRootArcs() {
        for ((name, number) in listOf("itu-t" to 0, "iso" to 1, "joint-iso-itu-t" to 2)) {
            assertValid("M { $name } DEFINITIONS ::= BEGIN END")
            assertValid("M { $name($number) 5 } DEFINITIONS ::= BEGIN END")
        }
    }

    /**
     * Use case: edge cases of names and numbers in the module header: zero, a very long number, a leading zero
     * and names with hyphens.
     * Expected: the parser accepts all of them (the annotator reports the leading zero).
     */
    fun testDefinitiveIdentifierNamesAndNumbers() {
        assertValid("M { 0 } DEFINITIONS ::= BEGIN END")
        assertValid("M { 0 123456789012345678901234567890 } DEFINITIONS ::= BEGIN END")
        assertValid("M { 007 } DEFINITIONS ::= BEGIN END")
        assertValid("M { joint-iso-itu-t(2) some-long-arc-name(10) x-1 y2(3) } DEFINITIONS ::= BEGIN END")
    }

    /**
     * Use case: a name instead of a number in parentheses of the module header, which only the object identifier
     * value (not the definitive identifier) allows.
     * Expected: an error is reported and the text is kept.
     */
    fun testNameInsteadOfNumberInDefinitiveIdentifier() {
        assertBroken("M { iso(value) } DEFINITIONS ::= BEGIN END")
        assertBroken("M { iso(some-value) 3 } DEFINITIONS ::= BEGIN END")
    }

    /**
     * Use case: every variant of the tag default.
     * Expected: each of the three tag defaults is a tag default element without error.
     */
    fun testTagDefaults() {
        for (tag in listOf("EXPLICIT", "IMPLICIT", "AUTOMATIC")) {
            val text = "M DEFINITIONS $tag TAGS ::= BEGIN END"
            assertValid(text)
            assertEquals(1, count(parse(text), AsnTypes.TAG_DEFAULT))
        }
    }

    /**
     * Use case: extension default and encoding reference default, alone and together with the tag default.
     * Expected: no error, every default is recognised.
     */
    fun testExtensionAndEncodingDefaults() {
        assertValid("M DEFINITIONS EXTENSIBILITY IMPLIED ::= BEGIN END")
        assertValid("M DEFINITIONS XER INSTRUCTIONS ::= BEGIN END")
        assertValid("M DEFINITIONS XER INSTRUCTIONS AUTOMATIC TAGS ::= BEGIN END")
        assertValid("M DEFINITIONS XER INSTRUCTIONS AUTOMATIC TAGS EXTENSIBILITY IMPLIED ::= BEGIN END")

        val file = parse("M DEFINITIONS XER INSTRUCTIONS IMPLICIT TAGS EXTENSIBILITY IMPLIED ::= BEGIN END")
        assertEquals(1, count(file, AsnTypes.ENCODING_REFERENCE_DEFAULT))
        assertEquals(1, count(file, AsnTypes.TAG_DEFAULT))
        assertEquals(1, count(file, AsnTypes.EXTENSION_DEFAULT))
    }

    /**
     * Use case: every combination of the three module defaults (encoding reference, tag default, extension
     * default): none, one, two or three of them, in every order, with each of the three tag defaults.
     * Expected: the parser accepts every combination without error and finds exactly the defaults that were written
     * (the annotator reports the wrong order).
     */
    fun testAllCombinationsOfDefaults() {
        val encoding = "XER INSTRUCTIONS"
        val extension = "EXTENSIBILITY IMPLIED"
        for (tag in listOf("EXPLICIT TAGS", "IMPLICIT TAGS", "AUTOMATIC TAGS")) {
            for (defaults in arrangements(listOf(encoding, tag, extension))) {
                val text = "M DEFINITIONS ${defaults.joinToString(" ")} ::= BEGIN END".replace("  ", " ")
                assertValid(text)
                val file = parse(text)
                assertEquals(text, if (encoding in defaults) 1 else 0, count(file, AsnTypes.ENCODING_REFERENCE_DEFAULT))
                assertEquals(text, if (tag in defaults) 1 else 0, count(file, AsnTypes.TAG_DEFAULT))
                assertEquals(text, if (extension in defaults) 1 else 0, count(file, AsnTypes.EXTENSION_DEFAULT))
            }
        }
    }

    /**
     * Use case: the complete first line with everything in it, spread over several lines with comments.
     * Expected: whitespace and comments between all tokens are allowed.
     */
    fun testCompleteHeaderWithCommentsAndLineBreaks() {
        assertValid(
            """
            My-Module { iso(1) -- root
              identified-organization(3) /* nested /* comment */ */ dod(6) 1 }
            DEFINITIONS XER INSTRUCTIONS
              AUTOMATIC TAGS
              EXTENSIBILITY IMPLIED
            ::= BEGIN END
            """.trimIndent(),
        )
    }

    /**
     * Use case: the module body already contains text (the body grammar does not exist yet).
     * Expected: the body is accepted without validation, including keywords such as `EXPLICIT` and braces.
     */
    fun testModuleBodyIsAcceptedUnchecked() {
        val text = "M DEFINITIONS ::= BEGIN Foo ::= SEQUENCE { a [0] EXPLICIT INTEGER, b BOOLEAN } END"
        assertValid(text)
        assertEquals(1, count(parse(text), AsnTypes.MODULE_BODY))
    }

    /**
     * Use case: a file with several modules.
     * Expected: every module is parsed on its own.
     */
    fun testSeveralModules() {
        val file = parse("A DEFINITIONS ::= BEGIN END\nB { 1 2 } DEFINITIONS AUTOMATIC TAGS ::= BEGIN END")
        assertFalse(PsiTreeUtil.hasErrorElements(file))
        assertEquals(2, count(file, AsnTypes.MODULE_DEFINITION))
    }

    /**
     * Use case: parts of the module header are missing or in the wrong place.
     * Expected: an error is reported, the text is kept completely and the already typed part stays in the tree.
     */
    fun testBrokenHeadersAreReported() {
        assertBroken("M")
        assertBroken("M DEFINITIONS")
        assertBroken("M DEFINITIONS ::=")
        assertBroken("M DEFINITIONS ::= BEGIN")
        assertBroken("M DEFINITIONS EXPLICIT ::= BEGIN END")
        assertBroken("M { } DEFINITIONS ::= BEGIN END")
        assertBroken("M { iso(1 } DEFINITIONS ::= BEGIN END")
        assertBroken("M { iso( DEFINITIONS ::= BEGIN END")
        assertBroken("M DEFINITIONS XER ::= BEGIN END")

        val file = parse("M { iso(1")
        assertEquals(1, count(file, AsnTypes.MODULE_IDENTIFIER))
        assertEquals(1, count(file, AsnTypes.DEFINITIVE_NAME_AND_NUMBER_FORM))
    }

    /**
     * Use case: unrelated text in front of a module.
     * Expected: the junk is reported as error, the module behind it is still found.
     */
    fun testJunkBeforeModule() {
        val file = parse("1 2 } ( M DEFINITIONS ::= BEGIN END")
        assertTrue(PsiTreeUtil.hasErrorElements(file))
        assertEquals(1, count(file, AsnTypes.MODULE_DEFINITION))
    }

    /**
     * Use case: the defaults of the module header are written in the wrong order or twice.
     * Expected: the parser accepts both without error (the annotator reports them).
     */
    fun testDefaultsInAnyOrderParse() {
        assertValid("M DEFINITIONS EXTENSIBILITY IMPLIED AUTOMATIC TAGS ::= BEGIN END")
        assertValid("M DEFINITIONS AUTOMATIC TAGS IMPLICIT TAGS ::= BEGIN END")
    }
}
