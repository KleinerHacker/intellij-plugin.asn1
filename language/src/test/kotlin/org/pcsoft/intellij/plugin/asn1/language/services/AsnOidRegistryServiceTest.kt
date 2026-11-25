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

package org.pcsoft.intellij.plugin.asn1.language.services

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.intellij.openapi.components.service
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import org.pcsoft.intellij.plugin.asn1.language.types.AsnOidNode

/**
 * Tests for [AsnOidRegistryService] and the registry file `oid/oid-registry.yaml` with its JSON Schema.
 */
class AsnOidRegistryServiceTest : BasePlatformTestCase() {
    private val mapper = YAMLMapper()

    private fun resource(path: String) =
        requireNotNull(AsnOidRegistryServiceTest::class.java.getResourceAsStream(path)) { "Missing resource $path" }

    private fun schema(): JsonSchema =
        JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012).getSchema(resource("/oid/oid-registry.schema.json"))

    private fun parse(yaml: String) = service<AsnOidRegistryService>().parse(yaml.byteInputStream())

    private fun errors(yaml: String): Set<String> =
        schema().validate(mapper.readTree(yaml)).map { it.message }.toSet()

    /**
     * Use case: the registry is loaded from the resource file that ships with the plugin.
     * Expected: the three root arcs with their numbers are present.
     */
    fun testLoadedRegistryHasRootArcs() {
        val roots = service<AsnOidRegistryService>().registry.roots
        assertEquals(listOf("itu-t" to 0L, "iso" to 1L, "joint-iso-itu-t" to 2L), roots.map { it.name to it.number })
    }

    /**
     * Use case: look up arcs several levels deep in the shipped registry.
     * Expected: `iso.identified-organization.dod.internet` has the numbers 1.3.6.1 and a description.
     */
    fun testLoadedRegistryHasDeepArcs() {
        val registry = service<AsnOidRegistryService>().registry
        val internet = registry.resolve(listOf("iso", "identified-organization", "dod", "internet"))
        assertNotNull(internet)
        assertEquals(registry.resolve(listOf("1", "3", "6", "1")), internet)
        assertFalse(internet?.description.isNullOrBlank())
    }

    /**
     * Use case: the registry is requested twice.
     * Expected: the same instance is returned, the file is read once.
     */
    fun testRegistryIsCached() {
        val service = service<AsnOidRegistryService>()
        assertSame(service.registry, service.registry)
    }

    /**
     * Use case: the shipped registry file is checked against its JSON Schema.
     * Expected: no violation.
     */
    fun testRegistryFileMatchesSchema() {
        val violations = schema().validate(mapper.readTree(resource("/oid/oid-registry.yaml")))
        assertTrue(violations.joinToString("\n") { it.message }, violations.isEmpty())
    }

    /**
     * Use case: the JSON Schema is applied to documents that break its rules: a missing required field, a name that
     * is no identifier, a negative number, an unknown property and a wrong type.
     * Expected: every document is rejected, so the schema is not toothless.
     */
    fun testSchemaRejectsInvalidDocuments() {
        listOf(
            "roots: [ { name: iso, number: 1 } ]",
            "roots: [ { name: Iso, number: 1, description: d } ]",
            "roots: [ { name: iso-, number: 1, description: d } ]",
            "roots: [ { name: iso, number: -1, description: d } ]",
            "roots: [ { name: iso, number: 1, description: d, other: 1 } ]",
            "roots: [ { name: iso, number: one, description: d } ]",
            "roots: [ { name: iso, number: 1, description: d, children: [ { name: x } ] } ]",
            "other: []",
            "{}",
        ).forEach { yaml -> assertFalse("Accepted: $yaml", errors(yaml).isEmpty()) }
    }

    /**
     * Use case: the JSON Schema is applied to a valid document with a nested arc.
     * Expected: no violation.
     */
    fun testSchemaAcceptsValidDocument() {
        assertTrue(errors("roots: [ { name: iso, number: 1, description: d, children: [ { name: a-b, number: 2, description: e } ] } ]").isEmpty())
    }

    /**
     * Use case: the rules the JSON Schema cannot express: among the children of one node (and among the roots)
     * every name and every number may appear only once.
     * Expected: the shipped registry has no duplicates.
     */
    fun testNamesAndNumbersAreUniquePerLevel() {
        fun check(path: String, nodes: List<AsnOidNode>) {
            assertEquals("Duplicate name below $path", nodes.size, nodes.map { it.name }.toSet().size)
            assertEquals("Duplicate number below $path", nodes.size, nodes.map { it.number }.toSet().size)
            nodes.forEach { check("$path.${it.name}", it.children) }
        }
        check("", service<AsnOidRegistryService>().registry.roots)
    }

    /**
     * Use case: a valid registry document is parsed, with and without `children` and `description`.
     * Expected: the data classes are filled, missing optional fields get their defaults.
     */
    fun testParseValid() {
        val registry = parse("roots:\n  - name: a\n    number: 1\n    description: A\n    children:\n      - name: b\n        number: 2\n")
        assertEquals("a", registry.roots.single().name)
        val child = registry.roots.single().children.single()
        assertEquals("b", child.name)
        assertEquals("", child.description)
        assertTrue(child.children.isEmpty())
    }

    /**
     * Use case: a document without any root, and a document with a root list that is empty.
     * Expected: an empty registry.
     */
    fun testParseEmptyRoots() {
        assertTrue(parse("roots: []").roots.isEmpty())
        assertTrue(parse("{}").roots.isEmpty())
    }

    /**
     * Use case: broken documents: empty text, unterminated list, wrong types, a missing required field, unknown
     * properties, no document.
     * Expected: an empty registry, no exception.
     */
    fun testParseBrokenDocuments() {
        listOf("", "roots: [", "roots: 5", "roots: [ { name: iso } ]", "unknown: 1", "- a\n- b", "\u0000\u0001").forEach { yaml ->
            assertTrue("Not empty: $yaml", parse(yaml).roots.isEmpty())
        }
    }

    /**
     * Use case: the stream cannot be read.
     * Expected: an empty registry, no exception.
     */
    fun testParseUnreadableStream() {
        val broken = object : java.io.InputStream() {
            override fun read(): Int = throw java.io.IOException("broken")
        }
        assertTrue(service<AsnOidRegistryService>().parse(broken).roots.isEmpty())
    }
}
