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

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.tree.IElementType
import org.pcsoft.intellij.plugin.asn1.PluginBundle
import org.pcsoft.intellij.plugin.asn1.language.parser.psi.AsnObjIdSupport
import org.pcsoft.intellij.plugin.asn1.language.parser.psi.AsnTypes

/**
 * PSI based highlighting and checks: module name and object identifier arcs get their color, numbers, stray
 * tokens and the defaults of a module header are checked. Works on incomplete trees; elements that are missing or
 * broken are simply skipped.
 */
class AsnAnnotator : Annotator, DumbAware {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (isStrayToken(element)) {
            holder.newAnnotation(HighlightSeverity.ERROR, PluginBundle.message("annotator.unexpected-token"))
                .range(element)
                .create()
            return
        }

        when (element.node?.elementType) {
            AsnTypes.UPPER_REFERENCE ->
                if (element.parent?.node?.elementType == AsnTypes.MODULE_IDENTIFIER) {
                    color(element, holder, AsnSyntaxHighlighter.MODULE_NAME)
                }

            AsnTypes.IDENTIFIER ->
                if (AsnObjIdSupport.isArcName(element)) {
                    color(element, holder, AsnSyntaxHighlighter.OID_ARC_NAME)
                }

            AsnTypes.NUMBER -> checkNumber(element, holder)
            AsnTypes.MODULE_DEFINITION -> checkHeaderDefaults(element, holder)
            AsnTypes.ASSIGNMENT_JUNK -> holder.newAnnotation(
                HighlightSeverity.ERROR,
                PluginBundle.message("annotator.assignment.junk"),
            ).range(element).create()

            AsnTypes.TYPE_ASSIGNMENT -> checkNotationPresent(element, AsnTypes.TYPE_NOTATION, holder)
            AsnTypes.VALUE_ASSIGNMENT -> checkNotationPresent(element, AsnTypes.VALUE_NOTATION, holder)
        }
    }

    /**
     * An assignment whose right hand side is missing (e.g. `Name ::=` while typing) is reported; the already typed
     * part stays untouched. Children are only looked at, never assumed.
     */
    private fun checkNotationPresent(assignment: PsiElement, notation: IElementType, holder: AnnotationHolder) {
        var child = assignment.firstChild
        while (child != null) {
            if (child.node?.elementType == notation) return
            child = child.nextSibling
        }
        holder.newAnnotation(HighlightSeverity.ERROR, PluginBundle.message("annotator.assignment.missing-notation"))
            .range(assignment)
            .create()
    }

    /**
     * A token directly below the file is a token that belongs to no module (text in front of or between modules).
     */
    private fun isStrayToken(element: PsiElement): Boolean =
        element.parent is PsiFile &&
                element.firstChild == null &&
                element !is PsiWhiteSpace &&
                element !is PsiComment &&
                element !is PsiErrorElement

    private fun color(element: PsiElement, holder: AnnotationHolder, key: TextAttributesKey) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element)
            .textAttributes(key)
            .create()
    }

    private fun checkNumber(element: PsiElement, holder: AnnotationHolder) {
        val text = element.text
        if (text.length > 1 && text.startsWith('0')) {
            holder.newAnnotation(HighlightSeverity.ERROR, PluginBundle.message("annotator.number.leading-zero"))
                .range(element)
                .create()
        }
    }

    /**
     * The parser accepts the defaults of the module header in any order and number. Here the required order
     * (encoding reference, tag default, extension default) and the uniqueness is checked.
     */
    private fun checkHeaderDefaults(module: PsiElement, holder: AnnotationHolder) {
        val seen = mutableSetOf<Int>()
        var highestRank = -1
        var child = module.firstChild
        while (child != null) {
            val rank = rankOf(child)
            if (rank != null) {
                if (!seen.add(rank)) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        PluginBundle.message("annotator.module.default.duplicate"),
                    ).range(child).create()
                } else if (rank < highestRank) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        PluginBundle.message("annotator.module.default.order"),
                    ).range(child).create()
                }
                highestRank = maxOf(highestRank, rank)
            }
            child = child.nextSibling
        }
    }

    private fun rankOf(element: PsiElement): Int? = when (element.node?.elementType) {
        AsnTypes.ENCODING_REFERENCE_DEFAULT -> 0
        AsnTypes.TAG_DEFAULT -> 1
        AsnTypes.EXTENSION_DEFAULT -> 2
        else -> null
    }
}
