package org.pcsoft.plugin.intellij.asn1.language.highlighting.syntax;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.highlighting.Asn1HighlighterScheme;
import org.pcsoft.plugin.intellij.asn1.language.parser.lexer.Asn1LexerAdapter;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1GenElementFactory;

/**
 *
 */
public class Asn1SyntaxHighlighter extends SyntaxHighlighterBase {

    private static final TextAttributesKey[] COMMENT = {Asn1HighlighterScheme.COMMENT};
    private static final TextAttributesKey[] NAME = {Asn1HighlighterScheme.NAME};
    private static final TextAttributesKey[] OPERATOR = {Asn1HighlighterScheme.OPERATOR};
    private static final TextAttributesKey[] BAD_CHARACTER = {Asn1HighlighterScheme.BAD_CHARACTER};
    private static final TextAttributesKey[] KEYWORD = {Asn1HighlighterScheme.KEYWORD};
    private static final TextAttributesKey[] NATIVE_TYPE = {Asn1HighlighterScheme.NATIVE_TYPE};
    private static final TextAttributesKey[] NUMBER = {Asn1HighlighterScheme.NUMBER};
    private static final TextAttributesKey[] SEPARATOR = {Asn1HighlighterScheme.SEPARATOR};
    private static final TextAttributesKey[] SYMBOL = new TextAttributesKey[]{Asn1HighlighterScheme.SYMBOL};
    private static final TextAttributesKey[] STRING = {Asn1HighlighterScheme.STRING};

    private static final TextAttributesKey[] EMPTY = {};

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new Asn1LexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType iElementType) {
        if (iElementType.equals(Asn1CustomElementFactory.COMMENT)) {
            return COMMENT;
        } else if (iElementType.equals(Asn1GenElementFactory.NAME_CAP) || iElementType.equals(Asn1GenElementFactory.NAME_NO_CAP) ||
                iElementType.equals(Asn1GenElementFactory.NAME_UPPER) || iElementType.equals(Asn1GenElementFactory.NAME_LOWER)) {
            return NAME;
        } else if (iElementType.equals(Asn1CustomElementFactory.OPERATOR)) {
            return OPERATOR;
        } else if (iElementType.equals(Asn1CustomElementFactory.BAD_CHARACTER)) {
            return BAD_CHARACTER;
        } else if (iElementType.equals(Asn1CustomElementFactory.KEYWORD)) {
            return KEYWORD;
        } else if (iElementType.equals(Asn1GenElementFactory.PRIMITIVE_TYPE) || iElementType.equals(Asn1GenElementFactory.LIST_TYPE)) {
            return NATIVE_TYPE;
        } else if (iElementType.equals(Asn1GenElementFactory.NUMBER)) {
            return NUMBER;
        } else if (iElementType.equals(Asn1CustomElementFactory.SEPARATOR)) {
            return SEPARATOR;
        } else if (iElementType.equals(Asn1CustomElementFactory.SYMBOL)) {
            return SYMBOL;
        } else if (iElementType.equals(Asn1GenElementFactory.TEXT) || iElementType.equals(Asn1CustomElementFactory.QUOTE)) {
            return STRING;
        }

        return EMPTY;
    }
}
