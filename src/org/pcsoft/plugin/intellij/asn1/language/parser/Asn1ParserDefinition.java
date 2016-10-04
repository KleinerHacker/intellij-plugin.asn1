package org.pcsoft.plugin.intellij.asn1.language.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;
import org.pcsoft.plugin.intellij.asn1.language.parser.lexer.Asn1LexerAdapter;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.Asn1File;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1GenElementFactory;

/**
 * Created by pfeifchr on 27.09.2016.
 */
public class Asn1ParserDefinition implements ParserDefinition {
    private static final IFileElementType FILE = new IFileElementType(Language.findInstance(Asn1Language.class));

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new Asn1LexerAdapter();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new Asn1Parser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return Asn1CustomElementFactory.WHITESPACE_SET;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return Asn1CustomElementFactory.COMMENT_SET;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode astNode) {
        return Asn1GenElementFactory.Factory.createElement(astNode);
    }

    @Override
    public PsiFile createFile(FileViewProvider fileViewProvider) {
        return new Asn1File(fileViewProvider);
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode astNode, ASTNode astNode1) {
        return SpaceRequirements.MAY;
    }
}
