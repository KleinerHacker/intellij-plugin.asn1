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

package org.pcsoft.intellij.plugin.asn1.language.types

/**
 * One arc of the object identifier tree, as written in `oid-registry.yaml`.
 *
 * @property name The name form of the arc, e.g. `iso`.
 * @property number The number form of the arc, e.g. `1`.
 * @property description Short description of the arc, shown in the completion popup.
 * @property children The arcs directly below this arc.
 */
data class AsnOidNode(
    val name: String,
    val number: Long,
    val description: String = "",
    val children: List<AsnOidNode> = emptyList(),
) {
    /**
     * The child arc that [key] denotes, or `null` if there is none. [key] is the name form (`internet`) or the
     * number form (`1`) of the arc.
     */
    fun child(key: String): AsnOidNode? = children.firstOrNull { it.matches(key) }

    /**
     * Whether [key] is the name form or the number form of this arc.
     */
    fun matches(key: String): Boolean = name == key || number.toString() == key
}

/**
 * The known part of the object identifier tree: the arcs below the root of the tree.
 *
 * @property roots The arcs directly below the root of the tree (`itu-t`, `iso`, `joint-iso-itu-t`).
 */
data class AsnOidRegistry(
    val roots: List<AsnOidNode> = emptyList(),
) {
    /**
     * The arc that [path] leads to, or `null` if the path is empty or one of its components is unknown. Every
     * component is the name form or the number form of an arc.
     */
    fun resolve(path: List<String>): AsnOidNode? {
        var node = roots.firstOrNull { path.firstOrNull()?.let(it::matches) == true } ?: return null
        for (key in path.drop(1)) {
            node = node.child(key) ?: return null
        }
        return node
    }

    /**
     * The arcs along [path], from the root arc to the last one, or an empty list if the path is empty or one of its
     * components is unknown. Every component is the name form or the number form of an arc.
     */
    fun trail(path: List<String>): List<AsnOidNode> {
        val result = ArrayList<AsnOidNode>()
        for (key in path) {
            val node = (if (result.isEmpty()) roots else result.last().children).firstOrNull { it.matches(key) }
                ?: return emptyList()
            result.add(node)
        }
        return result
    }

    /**
     * The arcs that may follow [path]: the root arcs for an empty path, the children of the arc that [path] leads
     * to otherwise. Empty if the path is unknown.
     */
    fun childrenOf(path: List<String>): List<AsnOidNode> =
        if (path.isEmpty()) roots else resolve(path)?.children ?: emptyList()
}
