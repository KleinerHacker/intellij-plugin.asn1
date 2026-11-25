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

package org.pcsoft.intellij.plugin.asn1.language

import com.intellij.openapi.fileTypes.LanguageFileType
import org.pcsoft.intellij.plugin.asn1.AsnIcons
import org.pcsoft.intellij.plugin.asn1.PluginBundle
import javax.swing.Icon

/**
 * File type of ASN.1 module definition files (`.asn`, `.asn1`).
 */
object AsnFileType : LanguageFileType(AsnLanguage) {
    /**
     * Default extension without leading dot.
     */
    const val DEFAULT_EXTENSION: String = "asn"

    override fun getName(): String = "ASN.1"

    override fun getDescription(): String = PluginBundle.message("filetype.asn1.description")

    override fun getDefaultExtension(): String = DEFAULT_EXTENSION

    override fun getIcon(): Icon = AsnIcons.FILE
}
