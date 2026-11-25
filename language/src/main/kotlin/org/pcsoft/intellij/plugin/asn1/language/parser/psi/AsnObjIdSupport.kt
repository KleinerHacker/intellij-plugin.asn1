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

package org.pcsoft.intellij.plugin.asn1.language.parser.psi

import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil

/**
 * Null-safe PSI queries around object identifiers. Works on incomplete trees: every query answers `false`
 * or `null` instead of failing when the expected structure is not there.
 */
object AsnObjIdSupport {
    /**
     * The state of an object identifier in front of a completion position.
     *
     * @property path The arcs already written in front of the position, each as its number form if it has one,
     * otherwise as its name form.
     * @property numberPrefix The number that is written directly in front of the position without a gap (the
     * part of an arc number typed so far), or an empty string.
     */
    data class ArcContext(val path: List<String>, val numberPrefix: String)

    /**
     * Whether [identifier], an `IDENTIFIER` leaf, is the name of an arc: the name form of a component of a
     * definitive identifier (module header) or of an object identifier value, alone or in front of a number.
     */
    fun isArcName(identifier: PsiElement): Boolean {
        if (identifier.node?.elementType != AsnTypes.IDENTIFIER) return false
        val nameForm = identifier.parent ?: return false
        if (nameForm.node?.elementType != AsnTypes.NAME_FORM) return false

        var component = nameForm.parent ?: return false
        if (component.node?.elementType == AsnTypes.DEFINITIVE_NAME_AND_NUMBER_FORM ||
            component.node?.elementType == AsnTypes.NAME_AND_NUMBER_FORM
        ) {
            component = component.parent ?: return false
        }
        val componentType = component.node?.elementType
        return componentType == AsnTypes.DEFINITIVE_OBJ_ID_COMPONENT || componentType == AsnTypes.OBJ_ID_COMPONENTS
    }

    /**
     * The arcs written in front of [position] inside the braces of a definitive identifier (module header) or of
     * an object identifier value, or `null` if [position] is not inside such braces or the text in front of it
     * cannot be read as a sequence of arcs (e.g. the position lies inside the parentheses of `iso(`).
     */
    fun arcContextBefore(position: PsiElement): ArcContext? {
        val tokens = ArrayList<PsiElement>()
        var leaf = PsiTreeUtil.prevLeaf(position, true)
        var open: PsiElement? = null
        while (leaf != null) {
            val type = leaf.node?.elementType
            if (type == AsnTypes.LBRACE) {
                open = leaf
                break
            }
            if (type == AsnTypes.RBRACE) return null
            if (leaf !is PsiWhiteSpace && leaf !is PsiComment) tokens.add(leaf)
            leaf = PsiTreeUtil.prevLeaf(leaf, true)
        }
        val openType = open?.parent?.node?.elementType
        if (openType != AsnTypes.DEFINITIVE_IDENTIFIER && openType != AsnTypes.OBJECT_IDENTIFIER_VALUE) return null

        tokens.reverse()
        // A number that touches the position is the part of an arc number that is typed right now.
        var numberPrefix = ""
        val last = tokens.lastOrNull()
        if (last != null && typeOf(last) == AsnTypes.NUMBER &&
            last.textRange.endOffset == position.textRange.startOffset
        ) {
            numberPrefix = last.text
            tokens.removeAt(tokens.size - 1)
        }

        val path = ArrayList<String>()
        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            when (typeOf(token)) {
                AsnTypes.NUMBER -> {
                    path.add(token.text)
                    i++
                }

                AsnTypes.IDENTIFIER ->
                    if (typeOf(tokens.getOrNull(i + 1)) == AsnTypes.LPAREN) {
                        // name form followed by its number form: name(number)
                        if (typeOf(tokens.getOrNull(i + 2)) != AsnTypes.NUMBER ||
                            typeOf(tokens.getOrNull(i + 3)) != AsnTypes.RPAREN
                        ) {
                            return null
                        }
                        path.add(tokens[i + 2].text)
                        i += 4
                    } else {
                        path.add(token.text)
                        i++
                    }

                else -> return null
            }
        }
        return ArcContext(path, numberPrefix)
    }

    /**
     * The path of the arc that the leaf [leaf] belongs to, including the arc itself, or `null` if [leaf] is no part of
     * an arc inside the braces of an object identifier. The leaf is the name of an arc, its number or, for the form
     * `name(number)`, the number between the parentheses. The arcs in front are given as their number form if they
     * have one.
     */
    fun arcPathOf(leaf: PsiElement): List<String>? {
        // The position of the arc (its name for name(number)) and the key that identifies it: a number between
        // parentheses is looked up by its own value, so a number that does not fit the name finds nothing.
        val start: PsiElement
        when (typeOf(leaf)) {
            AsnTypes.IDENTIFIER -> start = leaf
            AsnTypes.NUMBER -> {
                val before = prevSignificant(leaf)
                start = if (typeOf(before) == AsnTypes.LPAREN) {
                    prevSignificant(before)?.takeIf { typeOf(it) == AsnTypes.IDENTIFIER } ?: return null
                } else {
                    leaf
                }
            }

            else -> return null
        }
        val context = arcContextBefore(start) ?: return null
        if (context.numberPrefix.isNotEmpty()) return null
        return context.path + (if (typeOf(leaf) == AsnTypes.NUMBER) leaf.text else start.text)
    }

    private fun prevSignificant(element: PsiElement?): PsiElement? {
        var leaf = element?.let { PsiTreeUtil.prevLeaf(it, true) }
        while (leaf != null && (leaf is PsiWhiteSpace || leaf is PsiComment)) {
            leaf = PsiTreeUtil.prevLeaf(leaf, true)
        }
        return leaf
    }

    private fun typeOf(element: PsiElement?): IElementType? = element?.node?.elementType
}
