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

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.psi.TokenType
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.asn1.language.AsnFileType
import org.pcsoft.intellij.plugin.asn1.language.parser.psi.AsnTypes

/**
 * Tests for [AsnSyntaxHighlighter] and its factory: the token to color mapping and the fallback to the standard
 * keys of the color scheme (Java / Kotlin look).
 */
class AsnSyntaxHighlighterTest : BasePlatformTestCase() {
    private val highlighter = AsnSyntaxHighlighter()

    /**
     * Use case: the keywords of the module header.
     * Expected: they get the keyword key, which falls back to the standard keyword color.
     */
    fun testKeywords() {
        listOf(
            AsnTypes.DEFINITIONS, AsnTypes.BEGIN, AsnTypes.END, AsnTypes.EXPLICIT, AsnTypes.IMPLICIT,
            AsnTypes.AUTOMATIC, AsnTypes.TAGS, AsnTypes.EXTENSIBILITY, AsnTypes.IMPLIED, AsnTypes.INSTRUCTIONS,
        ).forEach { assertEquals(listOf(AsnSyntaxHighlighter.KEYWORD), highlighter.getTokenHighlights(it).toList()) }
        assertSame(DefaultLanguageHighlighterColors.KEYWORD, AsnSyntaxHighlighter.KEYWORD.fallbackAttributeKey)
    }

    /**
     * Use case: operators, numbers, comments and brackets.
     * Expected: each token type maps to its own key with the matching standard fallback.
     */
    fun testOtherTokens() {
        assertEquals(listOf(AsnSyntaxHighlighter.ASSIGNMENT), highlighter.getTokenHighlights(AsnTypes.ASSIGN).toList())
        assertEquals(listOf(AsnSyntaxHighlighter.NUMBER), highlighter.getTokenHighlights(AsnTypes.NUMBER).toList())
        assertEquals(listOf(AsnSyntaxHighlighter.LINE_COMMENT), highlighter.getTokenHighlights(AsnTypes.LINE_COMMENT).toList())
        assertEquals(listOf(AsnSyntaxHighlighter.BLOCK_COMMENT), highlighter.getTokenHighlights(AsnTypes.BLOCK_COMMENT).toList())
        assertEquals(listOf(AsnSyntaxHighlighter.BRACES), highlighter.getTokenHighlights(AsnTypes.LBRACE).toList())
        assertEquals(listOf(AsnSyntaxHighlighter.BRACES), highlighter.getTokenHighlights(AsnTypes.RBRACE).toList())
        assertEquals(listOf(AsnSyntaxHighlighter.BRACES), highlighter.getTokenHighlights(AsnTypes.LPAREN).toList())
        assertEquals(listOf(AsnSyntaxHighlighter.BRACES), highlighter.getTokenHighlights(AsnTypes.RPAREN).toList())

        assertSame(DefaultLanguageHighlighterColors.OPERATION_SIGN, AsnSyntaxHighlighter.ASSIGNMENT.fallbackAttributeKey)
        assertSame(DefaultLanguageHighlighterColors.NUMBER, AsnSyntaxHighlighter.NUMBER.fallbackAttributeKey)
        assertSame(DefaultLanguageHighlighterColors.LINE_COMMENT, AsnSyntaxHighlighter.LINE_COMMENT.fallbackAttributeKey)
        assertSame(DefaultLanguageHighlighterColors.BLOCK_COMMENT, AsnSyntaxHighlighter.BLOCK_COMMENT.fallbackAttributeKey)
        assertSame(DefaultLanguageHighlighterColors.BRACES, AsnSyntaxHighlighter.BRACES.fallbackAttributeKey)
    }

    /**
     * Use case: the colors that are set by the annotator (module name, object identifier arcs).
     * Expected: they fall back to class name and instance field of the standard scheme.
     */
    fun testAnnotatorKeysFallBackToStandardKeys() {
        assertSame(DefaultLanguageHighlighterColors.CLASS_NAME, AsnSyntaxHighlighter.MODULE_NAME.fallbackAttributeKey)
        assertSame(DefaultLanguageHighlighterColors.INSTANCE_FIELD, AsnSyntaxHighlighter.OID_ARC_NAME.fallbackAttributeKey)
    }

    /**
     * Use case: characters the lexer cannot assign and tokens without own color.
     * Expected: bad characters are highlighted as such, names and symbols get no key.
     */
    fun testBadCharactersAndUncoloredTokens() {
        assertEquals(listOf(AsnSyntaxHighlighter.BAD_CHARACTER), highlighter.getTokenHighlights(AsnTypes.BAD_CHARACTER).toList())
        assertEquals(listOf(AsnSyntaxHighlighter.BAD_CHARACTER), highlighter.getTokenHighlights(TokenType.BAD_CHARACTER).toList())
        assertSame(HighlighterColors.BAD_CHARACTER, AsnSyntaxHighlighter.BAD_CHARACTER.fallbackAttributeKey)
        assertTrue(highlighter.getTokenHighlights(AsnTypes.IDENTIFIER).isEmpty())
        assertTrue(highlighter.getTokenHighlights(AsnTypes.OTHER_SYMBOL).isEmpty())
    }

    /**
     * Use case: the platform asks for the highlighter of an ASN.1 file.
     * Expected: the factory is registered and delivers an [AsnSyntaxHighlighter] with the ASN.1 lexer.
     */
    fun testFactoryIsRegistered() {
        val created = SyntaxHighlighterFactory.getSyntaxHighlighter(AsnFileType, project, null)
        assertTrue(created is AsnSyntaxHighlighter)
        assertTrue(created!!.highlightingLexer is AsnLexerAdapter)
        assertTrue(AsnSyntaxHighlighterFactory().getSyntaxHighlighter(project, null) is AsnSyntaxHighlighter)
    }
}
