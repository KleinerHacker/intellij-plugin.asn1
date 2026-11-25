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

import com.intellij.psi.tree.IElementType
import org.pcsoft.intellij.plugin.asn1.language.AsnLanguage

/**
 * Type of a single lexer token of the ASN.1 language. The constants are generated into [AsnTypes].
 */
class AsnTokenType(debugName: String) : IElementType(debugName, AsnLanguage) {
    override fun toString(): String = "AsnTokenType.${super.toString()}"
}
