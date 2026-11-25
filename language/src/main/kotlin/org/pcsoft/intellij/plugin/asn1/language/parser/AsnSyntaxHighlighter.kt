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

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import org.pcsoft.intellij.plugin.asn1.language.parser.psi.AsnTypes

/**
 * Lexer based syntax highlighting. All keys fall back to the standard keys of the IntelliJ color scheme, so the
 * ASN.1 colors follow the Java / Kotlin look of the active scheme. Names that depend on the PSI context (module
 * name, object identifier arcs) are colored by the [AsnAnnotator].
 */
class AsnSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = AsnLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> = when (tokenType) {
        AsnTypes.DEFINITIONS, AsnTypes.BEGIN, AsnTypes.END, AsnTypes.EXPLICIT, AsnTypes.IMPLICIT,
        AsnTypes.AUTOMATIC, AsnTypes.TAGS, AsnTypes.EXTENSIBILITY, AsnTypes.IMPLIED, AsnTypes.INSTRUCTIONS ->
            KEYWORD_KEYS

        AsnTypes.ASSIGN -> ASSIGNMENT_KEYS
        AsnTypes.NUMBER -> NUMBER_KEYS
        AsnTypes.LINE_COMMENT -> LINE_COMMENT_KEYS
        AsnTypes.BLOCK_COMMENT -> BLOCK_COMMENT_KEYS
        AsnTypes.LBRACE, AsnTypes.RBRACE, AsnTypes.LPAREN, AsnTypes.RPAREN -> BRACES_KEYS
        AsnTypes.BAD_CHARACTER, TokenType.BAD_CHARACTER -> BAD_CHARACTER_KEYS
        else -> EMPTY_KEYS
    }

    /**
     * The text attribute keys of the ASN.1 language.
     */
    companion object {
        /**
         * Keywords such as `DEFINITIONS`, `BEGIN` or `AUTOMATIC`.
         */
        @JvmField
        val KEYWORD: TextAttributesKey = createTextAttributesKey("ASN1_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)

        /**
         * The assignment operator `::=`.
         */
        @JvmField
        val ASSIGNMENT: TextAttributesKey =
            createTextAttributesKey("ASN1_ASSIGNMENT", DefaultLanguageHighlighterColors.OPERATION_SIGN)

        /**
         * Numbers.
         */
        @JvmField
        val NUMBER: TextAttributesKey = createTextAttributesKey("ASN1_NUMBER", DefaultLanguageHighlighterColors.NUMBER)

        /**
         * Line comments `-- ...`.
         */
        @JvmField
        val LINE_COMMENT: TextAttributesKey =
            createTextAttributesKey("ASN1_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)

        /**
         * Block comments `/* ... */`.
         */
        @JvmField
        val BLOCK_COMMENT: TextAttributesKey =
            createTextAttributesKey("ASN1_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)

        /**
         * All braces: curly braces `{ }` and parentheses `( )`.
         */
        @JvmField
        val BRACES: TextAttributesKey = createTextAttributesKey("ASN1_BRACES", DefaultLanguageHighlighterColors.BRACES)

        /**
         * The name of a module in its definition (set by the annotator).
         */
        @JvmField
        val MODULE_NAME: TextAttributesKey =
            createTextAttributesKey("ASN1_MODULE_NAME", DefaultLanguageHighlighterColors.CLASS_NAME)

        /**
         * Name of an arc of an object identifier, e.g. `iso`, `dod` (set by the annotator).
         */
        @JvmField
        val OID_ARC_NAME: TextAttributesKey =
            createTextAttributesKey("ASN1_OID_ARC_NAME", DefaultLanguageHighlighterColors.INSTANCE_FIELD)

        /**
         * Characters the lexer cannot assign to any token.
         */
        @JvmField
        val BAD_CHARACTER: TextAttributesKey =
            createTextAttributesKey("ASN1_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        private val KEYWORD_KEYS = arrayOf(KEYWORD)
        private val ASSIGNMENT_KEYS = arrayOf(ASSIGNMENT)
        private val NUMBER_KEYS = arrayOf(NUMBER)
        private val LINE_COMMENT_KEYS = arrayOf(LINE_COMMENT)
        private val BLOCK_COMMENT_KEYS = arrayOf(BLOCK_COMMENT)
        private val BRACES_KEYS = arrayOf(BRACES)
        private val BAD_CHARACTER_KEYS = arrayOf(BAD_CHARACTER)
        private val EMPTY_KEYS = emptyArray<TextAttributesKey>()
    }
}
