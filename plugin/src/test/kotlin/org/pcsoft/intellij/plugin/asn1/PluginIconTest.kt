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

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Tests for the plugin icon `META-INF/pluginIcon.svg`.
 */
class PluginIconTest : BasePlatformTestCase() {
    /**
     * Use case: the marketplace and the IDE load the plugin icon from the plugin JAR.
     * Expected: the resource exists, is well-formed XML, embeds a PNG image and stays small.
     */
    fun testPluginIconIsEmbeddedRasterSvg() {
        val bytes = PluginIconTest::class.java.getResourceAsStream("/META-INF/pluginIcon.svg")!!.use { it.readBytes() }
        assertTrue("plugin icon too large: ${bytes.size}", bytes.size < 64 * 1024)

        val factory = DocumentBuilderFactory.newInstance().apply { isNamespaceAware = true }
        val root = factory.newDocumentBuilder().parse(bytes.inputStream()).documentElement
        assertEquals("svg", root.localName)

        val images = root.getElementsByTagNameNS("*", "image")
        assertEquals(1, images.length)
        val href = images.item(0).attributes.getNamedItemNS("http://www.w3.org/1999/xlink", "href").nodeValue
        assertTrue(href.startsWith("data:image/png;base64,"))
    }
}
