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

package org.pcsoft.intellij.plugin.asn1.language.feature.documentation

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.asn1.PluginBundle
import org.pcsoft.intellij.plugin.asn1.language.parser.psi.AsnObjIdSupport
import org.pcsoft.intellij.plugin.asn1.language.services.AsnOidRegistryService
import org.pcsoft.intellij.plugin.asn1.language.types.AsnOidNode

/**
 * Quick documentation for the arcs of an object identifier (module header and object identifier values). The
 * description of an arc comes from the registry `oid-registry.yaml`; an arc that is not in the registry has no
 * documentation.
 */
class AsnDocumentationProvider : AbstractDocumentationProvider() {
    /**
     * The leaf under the caret or mouse if it is part of a known arc, otherwise `null`.
     */
    override fun getCustomDocumentationElement(
        editor: Editor,
        file: PsiFile,
        contextElement: PsiElement?,
        targetOffset: Int,
    ): PsiElement? = contextElement?.takeIf { trailOf(it).isNotEmpty() }

    /**
     * One line with the arc and its dotted notation, e.g. `dod(6)  1.3.6`.
     */
    override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
        val trail = trailOf(element ?: originalElement)
        val last = trail.lastOrNull() ?: return null
        return StringUtil.escapeXmlEntities("${last.name}(${last.number})  ${dotted(trail)}")
    }

    /**
     * The documentation of the arc: its path in the ASN.1 form, its description, its name, its number and the dotted
     * notation of its path.
     */
    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        val trail = trailOf(element ?: originalElement)
        val last = trail.lastOrNull() ?: return null
        val builder = StringBuilder()
        builder.append(DocumentationMarkup.DEFINITION_START)
            .append(StringUtil.escapeXmlEntities("{ " + trail.joinToString(" ") { "${it.name}(${it.number})" } + " }"))
            .append(DocumentationMarkup.DEFINITION_END)
        if (last.description.isNotBlank()) {
            builder.append(DocumentationMarkup.CONTENT_START)
                .append(StringUtil.escapeXmlEntities(last.description))
                .append(DocumentationMarkup.CONTENT_END)
        }
        builder.append(DocumentationMarkup.SECTIONS_START)
        section(builder, PluginBundle.message("documentation.oid.arc-name"), last.name)
        section(builder, PluginBundle.message("documentation.oid.arc-number"), last.number.toString())
        section(builder, PluginBundle.message("documentation.oid.dotted-notation"), dotted(trail))
        builder.append(DocumentationMarkup.SECTIONS_END)
        return builder.toString()
    }

    private fun section(builder: StringBuilder, header: String, value: String) {
        builder.append(DocumentationMarkup.SECTION_HEADER_START)
            .append(StringUtil.escapeXmlEntities(header))
            .append(DocumentationMarkup.SECTION_SEPARATOR)
            .append("<p>").append(StringUtil.escapeXmlEntities(value))
            .append(DocumentationMarkup.SECTION_END)
    }

    private fun dotted(trail: List<AsnOidNode>): String = trail.joinToString(".") { it.number.toString() }

    /** The registry arcs from the root to the arc of [element], empty if [element] is no known arc. */
    private fun trailOf(element: PsiElement?): List<AsnOidNode> {
        val path = element?.let(AsnObjIdSupport::arcPathOf) ?: return emptyList()
        return service<AsnOidRegistryService>().registry.trail(path)
    }
}
