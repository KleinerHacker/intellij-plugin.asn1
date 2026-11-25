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

import com.intellij.psi.PsiElement

/**
 * Null-safe PSI queries around object identifiers. Works on incomplete trees: every query answers `false`
 * instead of failing when the expected structure is not there.
 */
object AsnObjIdSupport {
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
}
