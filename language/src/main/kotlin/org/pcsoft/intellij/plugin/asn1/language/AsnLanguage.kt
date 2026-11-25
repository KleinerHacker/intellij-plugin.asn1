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

import com.intellij.lang.Language

/**
 * The ASN.1 language (ITU-T X.680).
 */
object AsnLanguage : Language("ASN.1") {
    private fun readResolve(): Any = AsnLanguage

    override fun getDisplayName(): String = "ASN.1"
}
