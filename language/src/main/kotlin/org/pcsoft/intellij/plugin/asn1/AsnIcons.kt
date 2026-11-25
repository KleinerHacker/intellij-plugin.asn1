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

package org.pcsoft.intellij.plugin.asn1

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

/**
 * Collection of all icons of the `language` module.
 */
object AsnIcons {
    /**
     * Icon of ASN.1 files.
     */
    @JvmField
    val FILE: Icon = IconLoader.getIcon("/icons/asn1-file-icon.svg", AsnIcons::class.java)
}
