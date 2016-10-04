package org.pcsoft.plugin.intellij.asn1.language.parser.token;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 *
 */
public interface Asn1CustomElementFactory {
    IElementType WHITE_SPACE = new Asn1TokenType("WHITE_SPACE");
    IElementType LINE_BREAK = new Asn1TokenType("LINE_BREAK");
    IElementType COMMENT = new Asn1TokenType("COMMENT");
    IElementType BAD_CHARACTER = new Asn1TokenType("BAD_CHARACTER");

    IElementType KEYWORD = new Asn1TokenType("KEYWORD");
    IElementType OPERATOR = new Asn1TokenType("OPERATOR");
    IElementType SEPARATOR = new Asn1TokenType("SEPARATOR");
    IElementType SYMBOL = new Asn1TokenType("SYMBOL");
    IElementType QUOTE = new Asn1TokenType("QUOTE");

    IElementType BRACES_ROUND_OPEN = new Asn1TokenType("BRACES_ROUND_OPEN");
    IElementType BRACES_ROUND_CLOSE = new Asn1TokenType("BRACES_ROUND_CLOSE");
    IElementType BRACES_CORNER_OPEN = new Asn1TokenType("BRACES_CORNER_OPEN");
    IElementType BRACES_CORNER_CLOSE = new Asn1TokenType("BRACES_CORNER_CLOSE");
    IElementType BRACES_CURLY_OPEN = new Asn1TokenType("BRACES_CURLY_OPEN");
    IElementType BRACES_CURLY_CLOSE = new Asn1TokenType("BRACES_CURLY_CLOSE");

    TokenSet WHITESPACE_SET = TokenSet.create(Asn1CustomElementFactory.WHITE_SPACE, Asn1CustomElementFactory.LINE_BREAK);
    TokenSet COMMENT_SET = TokenSet.create(Asn1CustomElementFactory.COMMENT);
}
