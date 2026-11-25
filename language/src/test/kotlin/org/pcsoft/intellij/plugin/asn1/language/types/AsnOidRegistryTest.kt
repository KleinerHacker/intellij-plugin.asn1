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

import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for [AsnOidNode] and [AsnOidRegistry]: lookup of arcs by name form and number form.
 */
class AsnOidRegistryTest : BasePlatformTestCase() {
    private val registry = AsnOidRegistry(
        listOf(
            AsnOidNode(
                "iso", 1, "ISO",
                listOf(
                    AsnOidNode("member-body", 2, "Member bodies"),
                    AsnOidNode(
                        "identified-organization", 3, "Organizations",
                        listOf(AsnOidNode("dod", 6, "Department of Defense")),
                    ),
                ),
            ),
            AsnOidNode("itu-t", 0, "ITU-T"),
        ),
    )

    /**
     * Use case: look up a child arc of a node by its name form and by its number form.
     * Expected: both forms find the same arc.
     */
    fun testChildByNameAndNumber() {
        val iso = registry.roots[0]
        assertEquals("member-body", iso.child("member-body")?.name)
        assertEquals("member-body", iso.child("2")?.name)
    }

    /**
     * Use case: look up a child arc that does not exist, and a child of a node without children.
     * Expected: `null`, no exception.
     */
    fun testChildUnknown() {
        assertNull(registry.roots[0].child("unknown"))
        assertNull(registry.roots[0].child("99"))
        assertNull(registry.roots[1].child("1"))
        assertNull(registry.roots[0].child(""))
    }

    /**
     * Use case: resolve a path that consists of names, of numbers and of both mixed.
     * Expected: every spelling leads to the same arc.
     */
    fun testResolve() {
        assertEquals("dod", registry.resolve(listOf("iso", "identified-organization", "dod"))?.name)
        assertEquals("dod", registry.resolve(listOf("1", "3", "6"))?.name)
        assertEquals("dod", registry.resolve(listOf("iso", "3", "dod"))?.name)
        assertEquals("iso", registry.resolve(listOf("1"))?.name)
    }

    /**
     * Use case: resolve an empty path and paths with an unknown component at the start, in the middle and at the end.
     * Expected: `null` for all of them.
     */
    fun testResolveUnknown() {
        assertNull(registry.resolve(emptyList()))
        assertNull(registry.resolve(listOf("unknown")))
        assertNull(registry.resolve(listOf("iso", "unknown", "dod")))
        assertNull(registry.resolve(listOf("iso", "3", "7")))
        assertNull(registry.resolve(listOf("itu-t", "0")))
    }

    /**
     * Use case: ask for the arcs along a path, in every spelling, and for unknown or empty paths.
     * Expected: the arcs from the root to the last one; an empty list if the path is empty or unknown.
     */
    fun testTrail() {
        assertEquals(listOf("iso", "identified-organization", "dod"), registry.trail(listOf("1", "3", "dod")).map { it.name })
        assertEquals(listOf("iso"), registry.trail(listOf("iso")).map { it.name })
        assertTrue(registry.trail(emptyList()).isEmpty())
        assertTrue(registry.trail(listOf("iso", "99")).isEmpty())
        assertTrue(registry.trail(listOf("unknown", "3")).isEmpty())
    }

    /**
     * Use case: ask for the arcs that may follow a path.
     * Expected: the root arcs for the empty path, the children of the arc otherwise, nothing for an unknown path
     * or an arc without children.
     */
    fun testChildrenOf() {
        assertEquals(listOf("iso", "itu-t"), registry.childrenOf(emptyList()).map { it.name })
        assertEquals(listOf("member-body", "identified-organization"), registry.childrenOf(listOf("iso")).map { it.name })
        assertEquals(listOf("dod"), registry.childrenOf(listOf("1", "3")).map { it.name })
        assertTrue(registry.childrenOf(listOf("unknown")).isEmpty())
        assertTrue(registry.childrenOf(listOf("itu-t")).isEmpty())
    }

    /**
     * Use case: an empty registry, as it results from a missing or broken registry file.
     * Expected: nothing is found and nothing fails.
     */
    fun testEmptyRegistry() {
        val empty = AsnOidRegistry()
        assertTrue(empty.childrenOf(emptyList()).isEmpty())
        assertNull(empty.resolve(listOf("iso")))
    }
}
