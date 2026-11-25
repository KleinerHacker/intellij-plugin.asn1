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

package org.pcsoft.intellij.plugin.asn1.language.feature.completion

import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for [AsnCompletionContributor]: the tag default of the module header.
 */
class AsnCompletionContributorTest : BasePlatformTestCase() {
    private val tagDefaults = listOf("EXPLICIT TAGS", "IMPLICIT TAGS", "AUTOMATIC TAGS")

    private fun complete(text: String): List<String> {
        myFixture.configureByText("test.asn", text)
        myFixture.completeBasic()
        return myFixture.lookupElementStrings ?: emptyList()
    }

    /**
     * Use case: completion directly behind `DEFINITIONS`.
     * Expected: all three tag defaults are offered.
     */
    fun testOfferedBehindDefinitions() {
        val items = complete("Mod DEFINITIONS <caret>::= BEGIN END")
        assertContainsElements(items, tagDefaults)
    }

    /**
     * Use case: completion behind the encoding reference default `XER INSTRUCTIONS`.
     * Expected: all three tag defaults are offered.
     */
    fun testOfferedBehindInstructions() {
        val items = complete("Mod DEFINITIONS XER INSTRUCTIONS <caret>::= BEGIN END")
        assertContainsElements(items, tagDefaults)
    }

    /**
     * Use case: the module has an object identifier and the user completes behind `DEFINITIONS`.
     * Expected: the tag defaults are offered; comments and line breaks in between do not matter.
     */
    fun testOfferedWithObjectIdentifierAndComments() {
        val items = complete("Mod { iso(1) 3 } DEFINITIONS -- comment\n /* c */ <caret>::= BEGIN END")
        assertContainsElements(items, tagDefaults)
    }

    /**
     * Use case: the user has typed the start of one tag default.
     * Expected: the single matching phrase replaces the typed part completely.
     */
    fun testPrefixCompletesWholePhrase() {
        myFixture.configureByText("test.asn", "Mod DEFINITIONS EXP<caret> ::= BEGIN END")
        val items = myFixture.completeBasic()
        if (items != null) {
            assertTrue(myFixture.lookupElementStrings!!.contains("EXPLICIT TAGS"))
            assertFalse(myFixture.lookupElementStrings!!.contains("IMPLICIT TAGS"))
            myFixture.type('\n')
        }
        myFixture.checkResult("Mod DEFINITIONS EXPLICIT TAGS ::= BEGIN END")
    }

    /**
     * Use case: the module header has a tag default already, the caret stands in front of it.
     * Expected: no second tag default is offered.
     */
    fun testNotOfferedWhenTagDefaultExists() {
        val items = complete("Mod DEFINITIONS <caret> AUTOMATIC TAGS ::= BEGIN END")
        tagDefaults.forEach { assertFalse(items.contains(it)) }
    }

    /**
     * Use case: completion at positions that are not behind `DEFINITIONS` or `INSTRUCTIONS`: behind `::=`,
     * inside the object identifier, in the module name and in the body.
     * Expected: no tag default is offered.
     */
    fun testNotOfferedAtOtherPositions() {
        listOf(
            "Mod DEFINITIONS ::= <caret>BEGIN END",
            "Mod { <caret> } DEFINITIONS ::= BEGIN END",
            "<caret>Mod DEFINITIONS ::= BEGIN END",
            "Mod DEFINITIONS ::= BEGIN <caret> END",
            "Mod DEFINITIONS AUTOMATIC TAGS <caret>::= BEGIN END",
        ).forEach { text ->
            val items = complete(text)
            tagDefaults.forEach { assertFalse("$it offered in: $text", items.contains(it)) }
        }
    }

    /**
     * Use case: completion in incomplete text, such as a file that ends right behind `DEFINITIONS`.
     * Expected: the tag defaults are offered and nothing fails.
     */
    fun testIncompleteText() {
        assertContainsElements(complete("Mod DEFINITIONS <caret>"), tagDefaults)
        assertContainsElements(complete("Mod { 1 } DEFINITIONS <caret>"), tagDefaults)
        complete("<caret>")
        complete("{ <caret>")
        complete("/* <caret>")
    }

    private val moduleKeywords = listOf("DEFINITIONS", "BEGIN", "END")

    /**
     * Use case: completion directly behind the module name, with and without object identifier, comments and line
     * breaks in between.
     * Expected: `DEFINITIONS` is offered, `BEGIN` and `END` are not.
     */
    fun testDefinitionsOfferedBehindModuleName() {
        listOf(
            "Mod <caret>",
            "Mod <caret>::= BEGIN END",
            "Mod { iso(1) 3 } <caret>",
            "Mod { iso(1) 3 } -- c\n /* c */ <caret>",
        ).forEach { text ->
            val items = complete(text)
            assertTrue("DEFINITIONS missing in: $text", items.contains("DEFINITIONS"))
            assertFalse("BEGIN offered in: $text", items.contains("BEGIN"))
            assertFalse("END offered in: $text", items.contains("END"))
        }
    }

    /**
     * Use case: completion at positions where `DEFINITIONS` does not belong: at the start of the file, inside the
     * object identifier, behind `DEFINITIONS`, behind a complete module header and in the body.
     * Expected: `DEFINITIONS` is not offered.
     */
    fun testDefinitionsNotOfferedAtOtherPositions() {
        listOf(
            "<caret>",
            "<caret>Mod DEFINITIONS ::= BEGIN END",
            "Mod { <caret> } DEFINITIONS ::= BEGIN END",
            "Mod { 1 <caret>",
            "Mod DEFINITIONS <caret>",
            "Mod DEFINITIONS ::= <caret>",
            "Mod DEFINITIONS ::= BEGIN <caret>",
            "Mod DEFINITIONS ::= BEGIN END <caret>",
        ).forEach { text ->
            assertFalse("DEFINITIONS offered in: $text", complete(text).contains("DEFINITIONS"))
        }
    }

    /**
     * Use case: completion in front of an already written `DEFINITIONS`.
     * Expected: it is not offered a second time.
     */
    fun testDefinitionsNotOfferedTwice() {
        assertFalse(complete("Mod <caret> DEFINITIONS ::= BEGIN END").contains("DEFINITIONS"))
    }

    /**
     * Use case: completion directly behind `::=`, also with a comment in between.
     * Expected: `BEGIN` is offered, `DEFINITIONS` and `END` are not.
     */
    fun testBeginOfferedBehindAssign() {
        listOf("Mod DEFINITIONS ::= <caret>", "Mod DEFINITIONS ::= -- c\n <caret>").forEach { text ->
            val items = complete(text)
            assertTrue("BEGIN missing in: $text", items.contains("BEGIN"))
            assertFalse("DEFINITIONS offered in: $text", items.contains("DEFINITIONS"))
            assertFalse("END offered in: $text", items.contains("END"))
        }
    }

    /**
     * Use case: completion where `BEGIN` does not belong: in front of `::=`, behind a complete `BEGIN` and in
     * front of an existing `BEGIN`.
     * Expected: `BEGIN` is not offered.
     */
    fun testBeginNotOfferedAtOtherPositions() {
        listOf(
            "Mod DEFINITIONS <caret>::= BEGIN END",
            "Mod DEFINITIONS ::= BEGIN <caret>",
            "Mod DEFINITIONS ::= <caret> BEGIN END",
        ).forEach { text ->
            assertFalse("BEGIN offered in: $text", complete(text).contains("BEGIN"))
        }
    }

    /**
     * Use case: completion behind `BEGIN` and inside a module body that has no `END` yet.
     * Expected: `END` is offered, `DEFINITIONS` and `BEGIN` are not.
     */
    fun testEndOfferedInModuleBody() {
        listOf(
            "Mod DEFINITIONS ::= BEGIN <caret>",
            "Mod DEFINITIONS ::= BEGIN -- c\n <caret>",
            "Mod DEFINITIONS ::= BEGIN Foo ::= INTEGER <caret>",
        ).forEach { text ->
            val items = complete(text)
            assertTrue("END missing in: $text", items.contains("END"))
            assertFalse("BEGIN offered in: $text", items.contains("BEGIN"))
            assertFalse("DEFINITIONS offered in: $text", items.contains("DEFINITIONS"))
        }
    }

    /**
     * Use case: completion where `END` does not belong: in the module header, behind `::=` and in a module that
     * has its `END` already.
     * Expected: `END` is not offered.
     */
    fun testEndNotOfferedAtOtherPositions() {
        listOf(
            "Mod <caret>",
            "Mod DEFINITIONS <caret>",
            "Mod DEFINITIONS ::= <caret>",
            "Mod DEFINITIONS ::= BEGIN <caret> END",
            "Mod DEFINITIONS ::= BEGIN Foo ::= 1 <caret> END",
        ).forEach { text ->
            assertFalse("END offered in: $text", complete(text).contains("END"))
        }
    }

    /**
     * Use case: the user has typed the start of a structural keyword.
     * Expected: the keyword is completed.
     */
    fun testPrefixCompletesKeyword() {
        listOf(
            "Mod DEF<caret>" to "Mod DEFINITIONS",
            "Mod DEFINITIONS ::= BEG<caret>" to "Mod DEFINITIONS ::= BEGIN",
            "Mod DEFINITIONS ::= BEGIN EN<caret>" to "Mod DEFINITIONS ::= BEGIN END",
        ).forEach { (text, expected) ->
            myFixture.configureByText("test.asn", text)
            val items = myFixture.completeBasic()
            if (items != null) myFixture.type('\n')
            myFixture.checkResult(expected)
        }
    }

    /**
     * Use case: completion in a comment, in incomplete text and in a second module of the file.
     * Expected: nothing fails, no keyword is offered inside a comment and the second module is judged on its own.
     */
    fun testKeywordsInIncompleteAndSpecialText() {
        moduleKeywords.forEach { assertFalse(complete("Mod DEFINITIONS ::= BEGIN /* <caret>").contains(it)) }
        moduleKeywords.forEach { assertFalse(complete("Mod -- <caret>").contains(it)) }
        assertTrue(complete("A DEFINITIONS ::= BEGIN END\nB <caret>").contains("DEFINITIONS"))
        assertTrue(complete("A DEFINITIONS ::= BEGIN END\nB DEFINITIONS ::= BEGIN <caret>").contains("END"))
        complete("Mod {<caret>")
        complete("Mod { iso(<caret>")
    }
}
