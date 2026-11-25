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

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.psi.tree.IElementType
import org.pcsoft.intellij.plugin.asn1.language.parser.psi.AsnTypes

/**
 * Hand written parts of the parser, referenced from `AsnGrammar.bnf` as external rules.
 *
 * All functions only look ahead and never consume a token. They are total: any token sequence, including the end
 * of the input, yields a result without exception.
 */
object AsnParserUtil : GeneratedParserUtilBase() {
    /** Upper limit of tokens a lookahead inspects, so a long run without `::=` stays cheap. */
    private const val MAX_LOOKAHEAD = 64

    /**
     * External grammar rule `comma`: the current token is the symbol `,`.
     *
     * @param builder the builder of the running parse
     * @param level the grammar level (unused, required by the generated parser)
     * @return `true` and consumes the token, if the current token is `,`
     */
    @JvmStatic
    fun comma(builder: PsiBuilder, level: Int): Boolean = symbol(builder, ",")

    /**
     * External grammar rule `semicolon`: the current token is the symbol `;`.
     *
     * @param builder the builder of the running parse
     * @param level the grammar level (unused, required by the generated parser)
     * @return `true` and consumes the token, if the current token is `;`
     */
    @JvmStatic
    fun semicolon(builder: PsiBuilder, level: Int): Boolean = symbol(builder, ";")

    /**
     * External grammar rule `atAssignmentStart`: the upcoming tokens start a new assignment.
     *
     * That is an upper case reference directly followed by `::=` (type assignment) or an identifier followed by a
     * type start (upper case reference or `[`) and, on the same nesting level, a `::=` (value assignment). The
     * lookahead stops at `BEGIN`, `END` and an unmatched `}`; no token is consumed.
     *
     * @param builder the builder of the running parse
     * @param level the grammar level (unused, required by the generated parser)
     * @return `true`, if an assignment starts at the current token
     */
    @JvmStatic
    fun atAssignmentStart(builder: PsiBuilder, level: Int): Boolean {
        return when (builder.tokenType) {
            AsnTypes.UPPER_REFERENCE -> builder.lookAhead(1) == AsnTypes.ASSIGN
            AsnTypes.IDENTIFIER -> isValueAssignmentStart(builder)
            else -> false
        }
    }

    private fun symbol(builder: PsiBuilder, text: String): Boolean {
        if (builder.tokenType != AsnTypes.OTHER_SYMBOL || builder.tokenText != text) return false
        builder.advanceLexer()
        return true
    }

    private fun isValueAssignmentStart(builder: PsiBuilder): Boolean {
        val first = builder.lookAhead(1)
        if (first != AsnTypes.UPPER_REFERENCE && !isOpeningBracket(builder, 1)) return false

        var depth = 0
        var index = 1
        while (index <= MAX_LOOKAHEAD) {
            val type: IElementType = builder.lookAhead(index) ?: return false
            when (type) {
                AsnTypes.BEGIN, AsnTypes.END -> return false
                AsnTypes.LBRACE -> depth++
                AsnTypes.RBRACE -> if (depth == 0) return false else depth--
                // `name Type ::=` is ambiguous with "defined value, then type assignment" (`y` + `Z ::=`); the
                // value assignment wins, because the type of a value assignment looks exactly like that.
                AsnTypes.ASSIGN -> if (depth == 0) return true
            }
            index++
        }
        return false
    }

    private fun isOpeningBracket(builder: PsiBuilder, index: Int): Boolean {
        if (builder.lookAhead(index) != AsnTypes.OTHER_SYMBOL) return false
        // lookAhead does not expose the text; the bracket is detected with the raw token table instead.
        return bracketAt(builder, index)
    }

    /**
     * Finds the text of the n-th significant token ahead by walking the raw tokens and skipping whitespace and
     * comments, without consuming anything.
     */
    private fun bracketAt(builder: PsiBuilder, index: Int): Boolean {
        var significant = 0
        var raw = 0
        while (raw <= MAX_LOOKAHEAD * 4) {
            val type = builder.rawLookup(raw) ?: return false
            if (!isTrivia(builder, type)) {
                if (significant == index) {
                    // OTHER_SYMBOL is always exactly one character.
                    return builder.originalText.getOrNull(builder.rawTokenTypeStart(raw)) == '['
                }
                significant++
            }
            raw++
        }
        return false
    }

    private fun isTrivia(builder: PsiBuilder, type: IElementType): Boolean =
        type == com.intellij.psi.TokenType.WHITE_SPACE ||
                type == AsnTypes.LINE_COMMENT ||
                type == AsnTypes.BLOCK_COMMENT
}
