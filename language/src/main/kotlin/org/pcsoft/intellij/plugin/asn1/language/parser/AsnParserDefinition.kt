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

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.asn1.language.AsnFile
import org.pcsoft.intellij.plugin.asn1.language.AsnLanguage
import org.pcsoft.intellij.plugin.asn1.language.parser.psi.AsnTypes

/**
 * Parser definition of the ASN.1 language: lexer, parser, file and element creation.
 */
class AsnParserDefinition : ParserDefinition {
    override fun createLexer(project: Project?): Lexer = AsnLexerAdapter()

    override fun createParser(project: Project?): PsiParser = AsnParser()

    override fun getFileNodeType(): IFileElementType = FILE

    override fun getCommentTokens(): TokenSet = COMMENTS

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createElement(node: ASTNode): PsiElement = AsnTypes.Factory.createElement(node)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = AsnFile(viewProvider)

    /**
     * Shared constants of the parser definition.
     */
    companion object {
        /**
         * Element type of the root node of every ASN.1 file.
         */
        @JvmField
        val FILE: IFileElementType = IFileElementType(AsnLanguage)

        /**
         * Both comment forms: `-- ...` and `/* ... */`.
         */
        @JvmField
        val COMMENTS: TokenSet = TokenSet.create(AsnTypes.LINE_COMMENT, AsnTypes.BLOCK_COMMENT)
    }
}
