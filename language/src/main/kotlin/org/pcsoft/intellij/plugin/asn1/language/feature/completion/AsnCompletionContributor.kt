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

package org.pcsoft.intellij.plugin.asn1.language.feature.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.components.service
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.asn1.PluginBundle
import org.pcsoft.intellij.plugin.asn1.language.AsnLanguage
import org.pcsoft.intellij.plugin.asn1.language.parser.psi.AsnObjIdSupport
import org.pcsoft.intellij.plugin.asn1.language.parser.psi.AsnTypes
import org.pcsoft.intellij.plugin.asn1.language.services.AsnOidRegistryService

/**
 * Completion of the ASN.1 language. Offers the tag default of a module header
 * (`EXPLICIT TAGS`, `IMPLICIT TAGS`, `AUTOMATIC TAGS`), the structural keywords of a module definition
 * (`DEFINITIONS`, `BEGIN`, `END`) as long as the module does not contain them yet, and the known arcs inside an
 * object identifier.
 */
class AsnCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withLanguage(AsnLanguage),
            TagDefaultProvider,
        )
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withLanguage(AsnLanguage),
            ModuleKeywordProvider,
        )
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withLanguage(AsnLanguage),
            ObjIdArcProvider,
        )
    }

    /**
     * Offers the three tag defaults directly behind `DEFINITIONS` or `INSTRUCTIONS` of a module header.
     */
    object TagDefaultProvider : CompletionProvider<CompletionParameters>() {
        private val TAG_DEFAULTS: List<Pair<String, String>> = listOf(
            "EXPLICIT TAGS" to "completion.tag-default.explicit",
            "IMPLICIT TAGS" to "completion.tag-default.implicit",
            "AUTOMATIC TAGS" to "completion.tag-default.automatic",
        )

        override fun addCompletions(
            parameters: CompletionParameters,
            context: ProcessingContext,
            result: CompletionResultSet,
        ) {
            if (!isTagDefaultPosition(parameters.position)) return
            for ((phrase, messageKey) in TAG_DEFAULTS) {
                result.addElement(
                    LookupElementBuilder.create(phrase)
                        .bold()
                        .withTypeText(PluginBundle.message(messageKey), true),
                )
            }
        }

        /**
         * Whether a tag default may be written at [position]: the previous significant token is `DEFINITIONS`
         * or `INSTRUCTIONS`, and the module header does not contain a tag default already. Works on incomplete
         * trees (the position holds the dummy identifier of the completion system).
         */
        fun isTagDefaultPosition(position: PsiElement): Boolean {
            val previousType = previousSignificantLeaf(position)?.node?.elementType
            if (previousType != AsnTypes.DEFINITIONS && previousType != AsnTypes.INSTRUCTIONS) return false

            val module = generateSequence(position.parent) { it.parent }
                .firstOrNull { it.node?.elementType == AsnTypes.MODULE_DEFINITION }
                ?: return true

            var child = module.firstChild
            while (child != null) {
                if (child.node?.elementType == AsnTypes.TAG_DEFAULT && !PsiTreeUtil.isAncestor(child, position, false)) {
                    return false
                }
                child = child.nextSibling
            }
            return true
        }
    }

    /**
     * Offers the structural keywords `DEFINITIONS`, `BEGIN` and `END` of a module definition at the position where
     * the keyword is missing: `DEFINITIONS` behind the module name or its object identifier, `BEGIN` behind `::=`
     * and `END` behind `BEGIN` or inside the module body. A keyword the module contains already is not offered again.
     */
    object ModuleKeywordProvider : CompletionProvider<CompletionParameters>() {
        override fun addCompletions(
            parameters: CompletionParameters,
            context: ProcessingContext,
            result: CompletionResultSet,
        ) {
            val position = parameters.position
            if (position is PsiComment) return
            // The keyword that already follows the position (e.g. `::= <caret> BEGIN`) is not offered a second time,
            // even if error recovery of the dummy-identifier tree moved it out of the module.
            val nextType = nextSignificantLeaf(position)?.node?.elementType
            val keywords = listOf(
                Triple(
                    "DEFINITIONS", "completion.keyword.definitions",
                    isDefinitionsPosition(position) && nextType != AsnTypes.DEFINITIONS,
                ),
                Triple("BEGIN", "completion.keyword.begin", isBeginPosition(position) && nextType != AsnTypes.BEGIN),
                Triple("END", "completion.keyword.end", isEndPosition(position) && nextType != AsnTypes.END),
            )
            for ((keyword, messageKey, offered) in keywords) {
                if (!offered) continue
                result.addElement(
                    LookupElementBuilder.create(keyword)
                        .bold()
                        .withTypeText(PluginBundle.message(messageKey), true),
                )
            }
        }

        /**
         * Whether `DEFINITIONS` may be written at [position]: the previous significant token is the module name or
         * the closing brace of the module's object identifier, and the module has no `DEFINITIONS` yet.
         */
        fun isDefinitionsPosition(position: PsiElement): Boolean {
            val previous = previousSignificantLeaf(position) ?: return false
            val parentType = previous.parent?.node?.elementType
            val endsModuleIdentifier = (previous.node?.elementType == AsnTypes.UPPER_REFERENCE &&
                    parentType == AsnTypes.MODULE_IDENTIFIER) ||
                    (previous.node?.elementType == AsnTypes.RBRACE && parentType == AsnTypes.DEFINITIVE_IDENTIFIER)
            return endsModuleIdentifier && !moduleHas(previous, AsnTypes.DEFINITIONS)
        }

        /**
         * Whether `BEGIN` may be written at [position]: the previous significant token is `::=` of a module and the
         * module has no `BEGIN` yet.
         */
        fun isBeginPosition(position: PsiElement): Boolean {
            val previous = previousSignificantLeaf(position) ?: return false
            return previous.node?.elementType == AsnTypes.ASSIGN && !moduleHas(previous, AsnTypes.BEGIN)
        }

        /**
         * Whether `END` may be written at [position]: the previous significant token is `BEGIN` or lies inside the
         * module body, and the module has no `END` yet.
         */
        fun isEndPosition(position: PsiElement): Boolean {
            val previous = previousSignificantLeaf(position) ?: return false
            val behindBegin = previous.node?.elementType == AsnTypes.BEGIN
            val insideBody = PsiTreeUtil.findFirstParent(previous, false) {
                it.node?.elementType == AsnTypes.MODULE_BODY
            } != null
            return (behindBegin || insideBody) && !moduleHas(previous, AsnTypes.END)
        }

        /**
         * Whether the module definition around [element] directly contains a token of [type]. `false` outside of a
         * module definition, so nothing is offered there.
         */
        private fun moduleHas(element: PsiElement, type: IElementType): Boolean {
            val module = PsiTreeUtil.findFirstParent(element, false) {
                it.node?.elementType == AsnTypes.MODULE_DEFINITION
            } ?: return true
            var child = module.firstChild
            while (child != null) {
                if (child.node?.elementType == type) return true
                child = child.nextSibling
            }
            return false
        }
    }

    /**
     * Offers the known arcs (name form with number form, e.g. `internet(1)`) inside the braces of a definitive
     * identifier or an object identifier value. The arcs already written in front of the caret select the node of
     * the registry whose children are offered; at the start of the braces the root arcs are offered. Nothing is
     * offered for an unknown path.
     */
    object ObjIdArcProvider : CompletionProvider<CompletionParameters>() {
        override fun addCompletions(
            parameters: CompletionParameters,
            context: ProcessingContext,
            result: CompletionResultSet,
        ) {
            val position = parameters.position
            if (position is PsiComment) return
            val arcContext = AsnObjIdSupport.arcContextBefore(position) ?: return
            val children = service<AsnOidRegistryService>().registry.childrenOf(arcContext.path)
            if (children.isEmpty()) return

            // The number form is already in the text when the name is followed by `(`: insert only the name.
            val nameOnly = nextSignificantLeaf(position)?.node?.elementType == AsnTypes.LPAREN
            val target = if (arcContext.numberPrefix.isEmpty()) result else result.withPrefixMatcher(arcContext.numberPrefix)
            for (node in children) {
                val text = if (nameOnly) node.name else "${node.name}(${node.number})"
                target.addElement(
                    LookupElementBuilder.create(text)
                        .withLookupString(node.name)
                        .withLookupString(node.number.toString())
                        .withTypeText(node.description, true),
                )
            }
        }
    }
}

/**
 * The closest leaf in front of [element] that is neither whitespace nor a comment, or `null` at the start of the file.
 */
private fun previousSignificantLeaf(element: PsiElement): PsiElement? {
    var leaf = PsiTreeUtil.prevLeaf(element, true)
    while (leaf != null && (leaf is PsiWhiteSpace || leaf is PsiComment)) {
        leaf = PsiTreeUtil.prevLeaf(leaf, true)
    }
    return leaf
}

/**
 * The closest leaf behind [element] that is neither whitespace nor a comment, or `null` at the end of the file.
 */
private fun nextSignificantLeaf(element: PsiElement): PsiElement? {
    var leaf = PsiTreeUtil.nextLeaf(element, true)
    while (leaf != null && (leaf is PsiWhiteSpace || leaf is PsiComment)) {
        leaf = PsiTreeUtil.nextLeaf(leaf, true)
    }
    return leaf
}
