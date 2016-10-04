package org.pcsoft.plugin.intellij.asn1.language.highlighting.syntax;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.parser.lexer.Asn1LexerAdapter;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1GenElementFactory;

/**
 * Created by pfeifchr on 27.09.2016.
 */
public class Asn1SyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey OPERATOR = TextAttributesKey.createTextAttributesKey("ASN1_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey SEPARATOR = TextAttributesKey.createTextAttributesKey("ASN1_SEPARATOR", DefaultLanguageHighlighterColors.SEMICOLON);
    public static final TextAttributesKey SYMBOL = TextAttributesKey.createTextAttributesKey("ASN1_SYMBOL", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey NAME = TextAttributesKey.createTextAttributesKey("ASN1_NAME", DefaultLanguageHighlighterColors.CLASS_NAME);
    public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey("ASN1_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BAD_CHARACTER = TextAttributesKey.createTextAttributesKey("ASN1_NAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
    public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey("ASN1_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey NATIVE_TYPE = TextAttributesKey.createTextAttributesKey("ASN1_NATIVE_TYPE", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey("ASN1_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("ASN1_STRING", DefaultLanguageHighlighterColors.STRING);


    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new Asn1LexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType iElementType) {
        if (iElementType.equals(Asn1CustomElementFactory.COMMENT)) {
            return new TextAttributesKey[]{COMMENT};
        } else if (iElementType.equals(Asn1GenElementFactory.NAME)) {
            return new TextAttributesKey[]{NAME};
        } else if (iElementType.equals(Asn1CustomElementFactory.OPERATOR)) {
            return new TextAttributesKey[]{OPERATOR};
        } else if (iElementType.equals(Asn1CustomElementFactory.BAD_CHARACTER)) {
            return new TextAttributesKey[]{BAD_CHARACTER};
        } else if (iElementType.equals(Asn1CustomElementFactory.KEYWORD)) {
            return new TextAttributesKey[]{KEYWORD};
        } else if (iElementType.equals(Asn1GenElementFactory.PRIMITIVE_TYPE) || iElementType.equals(Asn1GenElementFactory.LIST_TYPE) ||
                iElementType.equals(Asn1GenElementFactory.LIST_OF_TYPE)) {
            return new TextAttributesKey[]{NATIVE_TYPE};
        } else if (iElementType.equals(Asn1GenElementFactory.NUMBER)) {
            return new TextAttributesKey[]{NUMBER};
        } else if (iElementType.equals(Asn1CustomElementFactory.SEPARATOR)) {
            return new TextAttributesKey[]{SEPARATOR};
        } else if (iElementType.equals(Asn1CustomElementFactory.SYMBOL)) {
            return new TextAttributesKey[]{SYMBOL};
        } else if (iElementType.equals(Asn1GenElementFactory.TEXT) || iElementType.equals(Asn1CustomElementFactory.QUOTE)) {
            return new TextAttributesKey[]{STRING};
        }

        return new TextAttributesKey[]{};
    }
}
