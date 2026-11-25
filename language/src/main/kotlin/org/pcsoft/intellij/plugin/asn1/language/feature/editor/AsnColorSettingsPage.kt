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

package org.pcsoft.intellij.plugin.asn1.language.feature.editor

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import org.pcsoft.intellij.plugin.asn1.AsnIcons
import org.pcsoft.intellij.plugin.asn1.PluginBundle
import org.pcsoft.intellij.plugin.asn1.language.parser.AsnSyntaxHighlighter
import javax.swing.Icon

/**
 * Color settings page of the ASN.1 language. Lists every highlighting role; the defaults are inherited from the
 * standard keys of the color scheme.
 */
class AsnColorSettingsPage : ColorSettingsPage {
    override fun getDisplayName(): String = PluginBundle.message("color-settings.display-name")

    override fun getIcon(): Icon = AsnIcons.FILE

    override fun getHighlighter(): SyntaxHighlighter = AsnSyntaxHighlighter()

    override fun getDemoText(): String = DEMO_TEXT

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> = TAGS

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = arrayOf(
        AttributesDescriptor(PluginBundle.message("color-settings.keyword"), AsnSyntaxHighlighter.KEYWORD),
        AttributesDescriptor(PluginBundle.message("color-settings.assignment"), AsnSyntaxHighlighter.ASSIGNMENT),
        AttributesDescriptor(PluginBundle.message("color-settings.number"), AsnSyntaxHighlighter.NUMBER),
        AttributesDescriptor(PluginBundle.message("color-settings.line-comment"), AsnSyntaxHighlighter.LINE_COMMENT),
        AttributesDescriptor(PluginBundle.message("color-settings.block-comment"), AsnSyntaxHighlighter.BLOCK_COMMENT),
        AttributesDescriptor(PluginBundle.message("color-settings.braces"), AsnSyntaxHighlighter.BRACES),
        AttributesDescriptor(PluginBundle.message("color-settings.module-name"), AsnSyntaxHighlighter.MODULE_NAME),
        AttributesDescriptor(PluginBundle.message("color-settings.oid-arc-name"), AsnSyntaxHighlighter.OID_ARC_NAME),
        AttributesDescriptor(PluginBundle.message("color-settings.bad-character"), AsnSyntaxHighlighter.BAD_CHARACTER),
    )

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    private companion object {
        val DEMO_TEXT: String = """
            <module>Example-Module</module> { <arc>iso</arc>(1) <arc>identified-organization</arc>(3) <arc>dod</arc>(6) 1 4 1 }
            DEFINITIONS AUTOMATIC TAGS EXTENSIBILITY IMPLIED ::= BEGIN

            -- A line comment
            /* A block comment */

            END
        """.trimIndent()

        val TAGS: Map<String, TextAttributesKey> = mapOf(
            "module" to AsnSyntaxHighlighter.MODULE_NAME,
            "arc" to AsnSyntaxHighlighter.OID_ARC_NAME,
        )
    }
}
