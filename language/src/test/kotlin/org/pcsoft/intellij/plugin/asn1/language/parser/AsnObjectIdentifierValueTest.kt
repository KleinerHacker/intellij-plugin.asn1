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

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilderFactory
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.asn1.language.parser.psi.AsnTypes

/**
 * Tests for the object identifier value as a rule of its own (`objectIdentifierValue`), parsed without any module
 * around it.
 */
class AsnObjectIdentifierValueTest : BasePlatformTestCase() {
    private fun parseValue(text: String): ASTNode {
        val definition = AsnParserDefinition()
        val builder = PsiBuilderFactory.getInstance().createBuilder(definition, definition.createLexer(project), text)
        return AsnParser().parse(AsnTypes.OBJECT_IDENTIFIER_VALUE, builder)
    }

    private fun count(node: ASTNode, type: IElementType): Int =
        (if (node.elementType == type) 1 else 0) + node.getChildren(null).sumOf { count(it, type) }

    private fun errors(node: ASTNode): Int = count(node, TokenType.ERROR_ELEMENT)

    private fun assertValid(text: String): ASTNode {
        val node = parseValue(text)
        assertEquals(text, node.text)
        assertEquals("Unexpected error in: $text", 0, errors(node))
        assertEquals(1, count(node, AsnTypes.OBJECT_IDENTIFIER_VALUE))
        return node
    }

    private fun assertBroken(text: String): ASTNode {
        val node = parseValue(text)
        assertEquals(text, node.text)
        assertTrue("Error expected in: $text", errors(node) > 0)
        return node
    }

    /**
     * Use case: an object identifier value consisting of numbers only.
     * Expected: one number form per component, no name, no error.
     */
    fun testNumberForm() {
        val node = assertValid("{ 1 3 6 1 }")
        assertEquals(4, count(node, AsnTypes.OBJ_ID_COMPONENTS))
        assertEquals(4, count(node, AsnTypes.NUMBER_FORM))
        assertEquals(0, count(node, AsnTypes.NAME_FORM))
    }

    /**
     * Use case: an object identifier value consisting of names only.
     * Expected: one name form per component, no number, no error.
     */
    fun testNameForm() {
        val node = assertValid("{ iso standard other-name }")
        assertEquals(3, count(node, AsnTypes.OBJ_ID_COMPONENTS))
        assertEquals(3, count(node, AsnTypes.NAME_FORM))
        assertEquals(0, count(node, AsnTypes.NUMBER_FORM))
    }

    /**
     * Use case: components in the name-and-number form, with a number and with a defined value in the parentheses.
     * Expected: every component is a name-and-number form, the defined value is recognised.
     */
    fun testNameAndNumberForm() {
        val node = assertValid("{ iso(1) arc(some-value) other(0) }")
        assertEquals(3, count(node, AsnTypes.OBJ_ID_COMPONENTS))
        assertEquals(3, count(node, AsnTypes.NAME_AND_NUMBER_FORM))
        assertEquals(3, count(node, AsnTypes.NAME_FORM))
        assertEquals(3, count(node, AsnTypes.NUMBER_FORM))
        assertEquals(1, count(node, AsnTypes.DEFINED_VALUE))
    }

    /**
     * Use case: all three component forms mixed in one value, with comments and line breaks between them.
     * Expected: every component is found in its own form.
     */
    fun testMixedForms() {
        val node = assertValid("{ iso(1) -- root\n 3 internet /* c */ other(some-value) }")
        assertEquals(4, count(node, AsnTypes.OBJ_ID_COMPONENTS))
        assertEquals(2, count(node, AsnTypes.NAME_AND_NUMBER_FORM))
        assertEquals(1, count(node, AsnTypes.DEFINED_VALUE))
    }

    /**
     * Use case: the three root arcs of the object identifier tree, as plain name and as name with number.
     * Expected: they are ordinary names for the parser, without error.
     */
    fun testRootArcs() {
        for (arc in listOf("itu-t" to 0, "iso" to 1, "joint-iso-itu-t" to 2)) {
            assertValid("{ ${arc.first} }")
            assertValid("{ ${arc.first}(${arc.second}) 5 }")
        }
    }

    /**
     * Use case: edge cases of numbers: zero, a very long number and a number with a leading zero.
     * Expected: the parser accepts all of them (the annotator reports the leading zero).
     */
    fun testNumberEdgeCases() {
        assertValid("{ 0 }")
        assertValid("{ 0 123456789012345678901234567890 }")
        assertValid("{ 007 }")
        assertValid("{ iso(007) }")
    }

    /**
     * Use case: an object identifier value without any component.
     * Expected: an error is reported, both braces stay in the tree.
     */
    fun testEmptyValueIsReported() {
        val node = assertBroken("{ }")
        assertEquals(1, count(node, AsnTypes.OBJECT_IDENTIFIER_VALUE))
    }

    /**
     * Use case: the user is in the middle of typing a component or misses a closing token.
     * Expected: an error is reported, the text stays complete and the already typed components stay in the tree.
     */
    fun testIncompleteValuesAreReported() {
        assertEquals(1, count(assertBroken("{ iso(1"), AsnTypes.NAME_AND_NUMBER_FORM))
        assertEquals(1, count(assertBroken("{ iso("), AsnTypes.NAME_AND_NUMBER_FORM))
        assertEquals(1, count(assertBroken("{ iso(1 3 }"), AsnTypes.NAME_AND_NUMBER_FORM))
        assertEquals(2, count(assertBroken("{ 1 2"), AsnTypes.NUMBER_FORM))
        assertEquals(1, count(assertBroken("{ iso"), AsnTypes.NAME_FORM))
        assertBroken("{")
    }

    /**
     * Use case: the opening brace is missing or the text is no object identifier value at all.
     * Expected: parsing does not fail and the tree covers the whole text.
     */
    fun testMissingOpeningBraceKeepsText() {
        for (text in listOf("iso(1) }", "}", "1 2 3", "(", "iso(1)")) {
            assertEquals(text, parseValue(text).text)
        }
    }

    /**
     * Use case: the user types a complete object identifier value character by character, or deletes it from the
     * start.
     * Expected: every prefix and every suffix parses without exception and the tree covers the whole text.
     */
    fun testEveryPrefixAndSuffixParses() {
        val text = "{ iso(1) identified-organization(3) 6 internet other-arc(some-value) /* c */ -- l\n}"
        for (length in 0..text.length) {
            assertEquals(text.substring(0, length), parseValue(text.substring(0, length)).text)
        }
        for (start in 0..text.length) {
            assertEquals(text.substring(start), parseValue(text.substring(start)).text)
        }
    }

    /**
     * Use case: unusual input for the rule: empty, only braces, only a comment, text ending inside a comment.
     * Expected: no exception and the whole text is covered.
     */
    fun testUnusualInput() {
        for (text in listOf("", " ", "{", "}", "{ {", "{ } }", "-- comment", "/* open", "{ /* open", "{ iso /*")) {
            assertEquals(text, parseValue(text).text)
        }
    }
}
