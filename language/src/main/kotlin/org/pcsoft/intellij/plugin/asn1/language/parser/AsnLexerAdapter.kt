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

package org.pcsoft.intellij.plugin.asn1.language.parser

import com.intellij.lexer.FlexAdapter

/**
 * Adapter of the generated ASN.1 lexer to the IntelliJ lexer API. The lexer is total and keeps no state beyond
 * the initial one (a block comment is one single token), so the editor may restart it at any token boundary.
 */
class AsnLexerAdapter : FlexAdapter(_AsnLexer())
