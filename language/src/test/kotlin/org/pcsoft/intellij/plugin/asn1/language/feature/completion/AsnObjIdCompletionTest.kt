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
 * Tests for the completion of the known object identifier arcs ([AsnCompletionContributor.ObjIdArcProvider]) in
 * the object identifier of a module header.
 */
class AsnObjIdCompletionTest : BasePlatformTestCase() {
    private val rootArcs = listOf("itu-t(0)", "iso(1)", "joint-iso-itu-t(2)")

    private fun complete(text: String): List<String> {
        myFixture.configureByText("test.asn", text)
        myFixture.completeBasic()
        return myFixture.lookupElementStrings ?: emptyList()
    }

    private fun module(oid: String) = "Mod { $oid } DEFINITIONS ::= BEGIN END"

    /**
     * Use case: completion at the start of the braces of the module's object identifier.
     * Expected: the three root arcs are offered with their numbers.
     */
    fun testRootArcsAtStart() {
        assertContainsElements(complete(module("<caret>")), rootArcs)
    }

    /**
     * Use case: completion behind a root arc written as name, as number and as name with number.
     * Expected: the children of `iso` are offered in every spelling.
     */
    fun testChildrenBehindRootArc() {
        listOf("iso <caret>", "1 <caret>", "iso(1) <caret>").forEach { oid ->
            val items = complete(module(oid))
            assertContainsElements(items, "member-body(2)", "identified-organization(3)")
            assertDoesntContain(items, rootArcs)
        }
    }

    /**
     * Use case: completion several levels deep, with mixed spellings of the arcs in front.
     * Expected: the children of `iso.identified-organization.dod.internet` are offered.
     */
    fun testDeepPath() {
        listOf("iso 3 6 1 <caret>", "iso(1) identified-organization(3) dod(6) internet(1) <caret>").forEach { oid ->
            assertContainsElements(complete(module(oid)), "directory(1)", "mgmt(2)", "private(4)", "snmpV2(6)")
        }
    }

    /**
     * Use case: completion where the registry offers exactly one arc (`dod` below `identified-organization`) and
     * the user has typed its first letter.
     * Expected: the arc is inserted at once, with its number form.
     */
    fun testSingleArcIsInserted() {
        myFixture.configureByText("test.asn", module("iso 3 d<caret>"))
        myFixture.completeBasic()
        myFixture.checkResult(module("iso 3 dod(6)"))
    }

    /**
     * Use case: completion where the registry offers exactly one arc and nothing is typed yet.
     * Expected: the arc is offered in the lookup (or inserted by the platform), in any case it ends up in the text
     * with its number form after it is chosen.
     */
    fun testSingleArcWithoutPrefix() {
        myFixture.configureByText("test.asn", module("iso 3 <caret>"))
        val items = myFixture.completeBasic()
        if (items != null) {
            assertEquals(listOf("dod(6)"), myFixture.lookupElementStrings)
            myFixture.type('\n')
        }
        myFixture.checkResult(module("iso 3 dod(6)"))
    }

    /**
     * Use case: the user has typed the start of an arc name.
     * Expected: the matching arc replaces the typed part, including the number form.
     */
    fun testNamePrefix() {
        myFixture.configureByText("test.asn", module("iso(1) mem<caret>"))
        myFixture.completeBasic()
        myFixture.checkResult(module("iso(1) member-body(2)"))
    }

    /**
     * Use case: the user has typed the number of an arc without a gap in front of the caret.
     * Expected: the arc with this number is offered and replaces the typed number.
     */
    fun testNumberPrefix() {
        myFixture.configureByText("test.asn", module("iso(1) 3<caret>"))
        myFixture.completeBasic()
        myFixture.checkResult(module("iso(1) identified-organization(3)"))
    }

    /**
     * Use case: the caret stands in front of the parentheses that already hold the number form.
     * Expected: only the name is offered, the number form is not written a second time.
     */
    fun testOnlyNameInFrontOfParenthesis() {
        val items = complete(module("iso <caret>(1)"))
        assertContainsElements(items, "member-body", "identified-organization")
        assertDoesntContain(items, listOf("member-body(2)", "identified-organization(3)"))
    }

    /**
     * Use case: comments and line breaks between the arcs.
     * Expected: they do not matter, the children of `iso` are offered.
     */
    fun testCommentsBetweenArcs() {
        assertContainsElements(complete(module("iso -- comment\n /* c */ <caret>")), "member-body(2)")
    }

    /**
     * Use case: the path in front of the caret is not in the registry.
     * Expected: no arc is offered.
     */
    fun testUnknownPath() {
        listOf("unknown <caret>", "iso 99 <caret>", "iso 3 6 1 99 <caret>", "iso 1 <caret>").forEach { oid ->
            val items = complete(module(oid))
            assertDoesntContain(items, rootArcs)
            assertFalse("Offered below $oid", items.any { it.contains("(") })
        }
    }

    /**
     * Use case: completion where no arc belongs: inside the parentheses of an arc, outside the braces, in the
     * module name, behind the closing brace and in the module body.
     * Expected: no arc is offered.
     */
    fun testNotOfferedAtOtherPositions() {
        listOf(
            "Mod { iso(<caret>) } DEFINITIONS ::= BEGIN END",
            "Mod { iso(1<caret>) } DEFINITIONS ::= BEGIN END",
            "Mod { iso(1) } <caret>DEFINITIONS ::= BEGIN END",
            "<caret>Mod { iso(1) } DEFINITIONS ::= BEGIN END",
            "Mod DEFINITIONS ::= BEGIN { <caret> } END",
            "Mod { iso(1) } DEFINITIONS ::= BEGIN <caret> END",
        ).forEach { text ->
            val items = complete(text)
            assertFalse("Arc offered in: $text", items.any { it.contains("(") })
        }
    }

    /**
     * Use case: completion in incomplete text: braces never closed, an open parenthesis, a file that ends in the
     * braces, only a brace, a comment.
     * Expected: nothing fails; where the position is inside the braces, the arcs are offered.
     */
    fun testIncompleteText() {
        assertContainsElements(complete("Mod { <caret>"), rootArcs)
        assertContainsElements(complete("Mod { iso(1) <caret>"), "member-body(2)")
        assertContainsElements(complete("Mod { iso <caret>"), "member-body(2)")
        complete("Mod { iso(<caret>")
        complete("Mod { iso(1<caret>")
        complete("{ <caret>")
        complete("<caret>")
        complete("/* { <caret>")
        complete("Mod { -- <caret>")
    }

    /**
     * Use case: the user types a complete module header character by character and asks for completion after every
     * character; also the empty file, a comment only, an opening brace only and text that ends in a comment.
     * Expected: no prefix of the text fails.
     */
    fun testEveryPrefixCompletes() {
        val text = "Mod { iso(1) identified-organization(3) dod 6 1 } DEFINITIONS ::= BEGIN END"
        for (length in 0..text.length) {
            complete(text.substring(0, length) + "<caret>")
        }
        listOf("", "-- comment", "{", "Mod {", "Mod { iso -- comment", "Mod { iso /* comment").forEach {
            complete("$it<caret>")
        }
    }
}
