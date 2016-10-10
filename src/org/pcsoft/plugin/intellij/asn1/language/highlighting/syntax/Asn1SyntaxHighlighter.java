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

import static org.pcsoft.plugin.intellij.asn1.language.highlighting.Asn1HighlighterScheme.NATIVE_TYPE;

/**
 *
 */
public class Asn1SyntaxHighlighter extends SyntaxHighlighterBase {
    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new Asn1LexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType iElementType) {
        if (iElementType.equals(Asn1CustomElementFactory.COMMENT)) {
            return new TextAttributesKey[]{Asn1HighlighterScheme.COMMENT};
        } else if (iElementType.equals(Asn1GenElementFactory.NAME_CAP) || iElementType.equals(Asn1GenElementFactory.NAME_NO_CAP) ||
                iElementType.equals(Asn1GenElementFactory.NAME_UPPER) || iElementType.equals(Asn1GenElementFactory.NAME_LOWER)) {
            return new TextAttributesKey[]{Asn1HighlighterScheme.NAME};
        } else if (iElementType.equals(Asn1CustomElementFactory.OPERATOR)) {
            return new TextAttributesKey[]{Asn1HighlighterScheme.OPERATOR};
        } else if (iElementType.equals(Asn1CustomElementFactory.BAD_CHARACTER)) {
            return new TextAttributesKey[]{Asn1HighlighterScheme.BAD_CHARACTER};
        } else if (iElementType.equals(Asn1CustomElementFactory.KEYWORD)) {
            return new TextAttributesKey[]{Asn1HighlighterScheme.KEYWORD};
        } else if (iElementType.equals(Asn1GenElementFactory.PRIMITIVE_TYPE) || iElementType.equals(Asn1GenElementFactory.LIST_TYPE)) {
            return new TextAttributesKey[]{NATIVE_TYPE};
        } else if (iElementType.equals(Asn1GenElementFactory.NUMBER)) {
            return new TextAttributesKey[]{Asn1HighlighterScheme.NUMBER};
        } else if (iElementType.equals(Asn1CustomElementFactory.SEPARATOR)) {
            return new TextAttributesKey[]{Asn1HighlighterScheme.SEPARATOR};
        } else if (iElementType.equals(Asn1CustomElementFactory.SYMBOL)) {
            return new TextAttributesKey[]{Asn1HighlighterScheme.SYMBOL};
        } else if (iElementType.equals(Asn1GenElementFactory.TEXT) || iElementType.equals(Asn1CustomElementFactory.QUOTE)) {
            return new TextAttributesKey[]{Asn1HighlighterScheme.STRING};
        }

        return new TextAttributesKey[]{};
    }
}
