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

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.asn1.language.parser.psi.AsnTypes

/**
 * Tests for the ASN.1 lexer ([AsnLexerAdapter] on top of the generated lexer).
 */
class AsnLexerTest : BasePlatformTestCase() {
    private fun lex(text: String): List<Pair<IElementType, String>> {
        val lexer = AsnLexerAdapter()
        lexer.start(text)
        val tokens = mutableListOf<Pair<IElementType, String>>()
        while (true) {
            val type = lexer.tokenType ?: break
            if (type != TokenType.WHITE_SPACE) {
                tokens += type to text.substring(lexer.tokenStart, lexer.tokenEnd)
            }
            lexer.advance()
        }
        return tokens
    }

    private fun types(text: String): List<IElementType> = lex(text).map { it.first }

    /**
     * Use case: a module header is typed with all of its keywords.
     * Expected: every keyword gets its own token type, `::=` is the assignment token.
     */
    fun testKeywords() {
        assertEquals(
            listOf(
                AsnTypes.DEFINITIONS, AsnTypes.BEGIN, AsnTypes.END, AsnTypes.EXPLICIT, AsnTypes.IMPLICIT,
                AsnTypes.AUTOMATIC, AsnTypes.TAGS, AsnTypes.EXTENSIBILITY, AsnTypes.IMPLIED,
                AsnTypes.INSTRUCTIONS, AsnTypes.ASSIGN,
            ),
            types("DEFINITIONS BEGIN END EXPLICIT IMPLICIT AUTOMATIC TAGS EXTENSIBILITY IMPLIED INSTRUCTIONS ::="),
        )
    }

    /**
     * Use case: an object identifier is typed.
     * Expected: braces, parentheses, numbers and names are separate tokens, upper case names are references,
     * lower case names are identifiers.
     */
    fun testObjectIdentifierTokens() {
        assertEquals(
            listOf(
                AsnTypes.LBRACE, AsnTypes.IDENTIFIER, AsnTypes.LPAREN, AsnTypes.NUMBER, AsnTypes.RPAREN,
                AsnTypes.IDENTIFIER, AsnTypes.NUMBER, AsnTypes.RBRACE, AsnTypes.UPPER_REFERENCE,
            ),
            types("{ iso(1) identified-organization 3 } Module"),
        )
    }

    /**
     * Use case: names with hyphens (`Foo-Bar`) and a name followed directly by a comment (`Foo--c`).
     * Expected: single hyphens belong to the name, a double hyphen starts a comment.
     */
    fun testHyphenRules() {
        assertEquals(listOf(AsnTypes.UPPER_REFERENCE to "Foo-Bar"), lex("Foo-Bar"))
        assertEquals(
            listOf(AsnTypes.UPPER_REFERENCE to "Foo", AsnTypes.LINE_COMMENT to "--c"),
            lex("Foo--c"),
        )
        assertEquals(
            listOf(AsnTypes.UPPER_REFERENCE to "Foo", AsnTypes.OTHER_SYMBOL to "-"),
            lex("Foo-"),
        )
    }

    /**
     * Use case: a line comment ends at the end of the line or at the next double hyphen.
     * Expected: the text behind the closing `--` is lexed as regular tokens again.
     */
    fun testLineComments() {
        assertEquals(listOf(AsnTypes.LINE_COMMENT to "-- text"), lex("-- text"))
        assertEquals(
            listOf(AsnTypes.LINE_COMMENT to "-- a - b --", AsnTypes.NUMBER to "1"),
            lex("-- a - b -- 1"),
        )
        assertEquals(
            listOf(AsnTypes.LINE_COMMENT to "-- text", AsnTypes.NUMBER to "2"),
            lex("-- text\n2"),
        )
    }

    /**
     * Use case: block comments, also nested ones.
     * Expected: the whole comment, with all nesting levels, is exactly one token.
     */
    fun testBlockComments() {
        assertEquals(listOf(AsnTypes.BLOCK_COMMENT to "/* a */"), lex("/* a */"))
        assertEquals(
            listOf(AsnTypes.BLOCK_COMMENT to "/* a /* b */ c */", AsnTypes.NUMBER to "1"),
            lex("/* a /* b */ c */ 1"),
        )
    }

    /**
     * Use case: the user has typed the opening delimiter of a block comment but not yet the end of the comment (also nested and unbalanced).
     * Expected: the comment runs to the end of the input, the lexer does not fail.
     */
    fun testUnterminatedBlockComment() {
        assertEquals(listOf(AsnTypes.BLOCK_COMMENT to "/* open"), lex("/* open"))
        assertEquals(listOf(AsnTypes.BLOCK_COMMENT to "/* a /* b */ c"), lex("/* a /* b */ c"))
        assertEquals(listOf(AsnTypes.BLOCK_COMMENT to "/*"), lex("/*"))
        assertEquals(listOf(AsnTypes.BLOCK_COMMENT to "/*/"), lex("/*/"))
    }

    /**
     * Use case: incomplete or foreign characters are typed.
     * Expected: `::` and non-ASCII characters are bad characters, ASCII punctuation is an ordinary symbol,
     * nothing makes the lexer fail.
     */
    fun testBadAndOtherCharacters() {
        assertEquals(listOf(AsnTypes.BAD_CHARACTER to "::"), lex("::"))
        assertEquals(listOf(AsnTypes.OTHER_SYMBOL to ":"), lex(":"))
        assertEquals(listOf(AsnTypes.BAD_CHARACTER to "§"), lex("§"))
        assertEquals(
            listOf(AsnTypes.OTHER_SYMBOL to ",", AsnTypes.OTHER_SYMBOL to ";", AsnTypes.OTHER_SYMBOL to "|"),
            lex(", ; |"),
        )
    }

    /**
     * Use case: numbers with leading zeros are typed.
     * Expected: the lexer still produces one number token; the annotator reports the problem.
     */
    fun testNumbers() {
        assertEquals(listOf(AsnTypes.NUMBER to "0", AsnTypes.NUMBER to "123", AsnTypes.NUMBER to "007"), lex("0 123 007"))
    }

    /**
     * Use case: the editor restarts the lexer at an arbitrary token boundary for incremental highlighting.
     * Expected: the lexer is in the initial state at every token boundary, also after a block comment.
     */
    fun testLexerIsStatelessBetweenTokens() {
        val lexer = AsnLexerAdapter()
        lexer.start("A /* x /* y */ z */ B {")
        while (lexer.tokenType != null) {
            assertEquals(0, lexer.state)
            lexer.advance()
        }
    }

    /**
     * Use case: empty input.
     * Expected: no token at all.
     */
    fun testEmptyInput() {
        assertTrue(lex("").isEmpty())
    }
}
