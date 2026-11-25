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

package org.pcsoft.intellij.plugin.asn1.language.feature.editor

import com.intellij.codeInsight.highlighting.BraceMatchingUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.asn1.language.parser.psi.AsnTypes

/**
 * Tests for [AsnBraceMatcher].
 */
class AsnBraceMatcherTest : BasePlatformTestCase() {
    /**
     * Use case: the platform asks which brace pairs ASN.1 knows.
     * Expected: `{` with `}` and `(` with `)`, both structural.
     */
    fun testPairs() {
        val pairs = AsnBraceMatcher().pairs
        assertEquals(2, pairs.size)
        assertEquals(AsnTypes.LBRACE, pairs[0].leftBraceType)
        assertEquals(AsnTypes.RBRACE, pairs[0].rightBraceType)
        assertTrue(pairs[0].isStructural)
        assertEquals(AsnTypes.LPAREN, pairs[1].leftBraceType)
        assertEquals(AsnTypes.RPAREN, pairs[1].rightBraceType)
        assertTrue(pairs[1].isStructural)
    }

    /**
     * Use case: the caret stands behind the closing parenthesis of a name with number, and in front of the opening one.
     * Expected: the partner parenthesis is found in both directions.
     */
    fun testMatchingParenthesisIsFound() {
        myFixture.configureByText("test.asn", "M { iso<caret>(1) 3 } DEFINITIONS ::= BEGIN END")
        assertEquals("M { iso(1".length, BraceMatchingUtil.getMatchedBraceOffset(myFixture.editor, true, myFixture.file))
        myFixture.configureByText("test.asn", "M { iso(1<caret>) 3 } DEFINITIONS ::= BEGIN END")
        assertEquals("M { iso".length, BraceMatchingUtil.getMatchedBraceOffset(myFixture.editor, false, myFixture.file))
    }

    /**
     * Use case: auto-closing of a brace and the start of a code construct.
     * Expected: pairs are allowed in front of any token, the construct starts at the brace itself.
     */
    fun testPairedBraceSettings() {
        val matcher = AsnBraceMatcher()
        assertTrue(matcher.isPairedBracesAllowedBeforeType(AsnTypes.LBRACE, null))
        assertTrue(matcher.isPairedBracesAllowedBeforeType(AsnTypes.LPAREN, AsnTypes.NUMBER))
        assertEquals(17, matcher.getCodeConstructStart(null, 17))
    }

    /**
     * Use case: the caret stands in front of the opening brace of an object identifier with nested parentheses.
     * Expected: the matching closing brace is found.
     */
    fun testMatchingBraceIsFound() {
        val text = "M <caret>{ iso(1) 3 } DEFINITIONS ::= BEGIN END"
        myFixture.configureByText("test.asn", text)
        val matched = BraceMatchingUtil.getMatchedBraceOffset(myFixture.editor, true, myFixture.file)
        assertEquals("M { iso(1) 3 ".length, matched)
    }

    /**
     * Use case: the closing brace is missing while the user is typing.
     * Expected: the file is parsed and highlighted without failure; there is simply no partner.
     */
    fun testMissingPartnerDoesNotFail() {
        myFixture.configureByText("test.asn", "M <caret>{ iso(1")
        assertNotNull(myFixture.file)
        assertNotNull(myFixture.doHighlighting())
    }
}
