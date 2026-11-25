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

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.intellij.openapi.diagnostic.Logger
import org.pcsoft.intellij.plugin.asn1.language.types.AsnOidRegistry
import java.io.IOException
import java.io.InputStream

/**
 * Application service that provides the known object identifier arcs. The registry is read once from the
 * resource `/oid/oid-registry.yaml`; a missing or broken file yields an empty registry (and a log entry), never an
 * exception.
 */
class AsnOidRegistryService {
    /**
     * The known arcs of the object identifier tree, loaded on first access.
     */
    val registry: AsnOidRegistry by lazy { load() }

    /**
     * Reads a registry from [stream] (YAML, format of `oid-registry.schema.json`). An unreadable or invalid
     * document yields an empty registry.
     */
    fun parse(stream: InputStream): AsnOidRegistry =
        try {
            stream.use { MAPPER.readValue(it, AsnOidRegistry::class.java) } ?: AsnOidRegistry()
        } catch (e: IOException) {
            LOG.warn("Cannot read the object identifier registry", e)
            AsnOidRegistry()
        }

    private fun load(): AsnOidRegistry {
        val stream = AsnOidRegistryService::class.java.getResourceAsStream(RESOURCE)
        if (stream == null) {
            LOG.warn("Object identifier registry not found: $RESOURCE")
            return AsnOidRegistry()
        }
        return parse(stream)
    }

    private companion object {
        const val RESOURCE = "/oid/oid-registry.yaml"
        val LOG = Logger.getInstance(AsnOidRegistryService::class.java)
        // A missing required field (e.g. `number`) must be an error, not silently become 0.
        val MAPPER: YAMLMapper = YAMLMapper.builder()
            .addModule(KotlinModule.Builder().build())
            .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
            .build()
    }
}
