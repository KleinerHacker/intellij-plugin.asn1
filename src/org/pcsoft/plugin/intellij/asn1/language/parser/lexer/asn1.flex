package org.pcsoft.plugin.intellij.asn1.language.parser.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1ElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;

%%

%class Asn1Lexer
%implements FlexLexer
%unicode
%function advance
%type IElementType

%state STRING

CRLF=\n|\r|\r\n
WHITE_SPACE=[\ \t\f]
COMMENT_LINE="--"[^\r\n]*
COMMENT_BLOCK="/*"[^"*/"]"*/"
NAME=[a-zA-Z_]{1}[0-9a-zA-Z_\-]*
NUMBER=[\-]?[0-9]*([\.][0-9]*)?
TEXT=[^\"]*

OPERATOR= "::="
SEPARATOR="," | ";" | ":" | "."
SYMBOL="&" | "@"
QUOTE=\"

BR_ROUND_OPEN="("
BR_ROUND_CLOSE=")"
BR_CURLY_OPEN="{"
BR_CURLY_CLOSE="}"
BR_CORNER_OPEN="["
BR_CORNER_CLOSE="]"

KEYWORD="DEFINITIONS" | "IMPLICIT" | "EXPLICIT" | "AUTOMATIC" | "TAGS" | "BEGIN" | "END" | "APPLICATION" | "IMPORTS" | "FROM" | "CLASS" | "UNIQUE" | "OPTIONAL"
PRIMITIVE_TYPE="INTEGER" | "BIT STRING" | "BOOLEAN" | "OCTET STRING" | "OBJECT IDENTIFIER" | "PrintableString" | "IA5String" | "UTF8String" | "T61String" | "GeneralizedTime" | "UTCTime" | "VideoTexString"
LIST_TYPE="SEQUENCE" | "SET"
LIST_OF_TYPE="SEQUENCE OF" | "SET OF"

%%

{COMMENT_LINE}                                  { return Asn1CustomElementFactory.COMMENT; }
{COMMENT_BLOCK}                                 { return Asn1CustomElementFactory.COMMENT; }

{KEYWORD}                                       { return Asn1CustomElementFactory.KEYWORD; }
{PRIMITIVE_TYPE}                                { return Asn1ElementFactory.PRIMITIVE_TYPE; }
{LIST_OF_TYPE}                                  { return Asn1ElementFactory.LIST_OF_TYPE; }
{LIST_TYPE}                                     { return Asn1ElementFactory.LIST_TYPE; }
{OPERATOR}                                      { return Asn1CustomElementFactory.OPERATOR; }
{SEPARATOR}                                     { return Asn1CustomElementFactory.SEPARATOR; }
{SYMBOL}                                        { return Asn1CustomElementFactory.SYMBOL; }
{QUOTE}                                         { if (yystate() == STRING) yybegin(YYINITIAL); else yybegin(STRING); return Asn1CustomElementFactory.QUOTE; }

{NUMBER}                                        { return Asn1ElementFactory.NUMBER; }
<STRING> {
    {TEXT}                                      { return Asn1ElementFactory.TEXT; }
}
{NAME}                                          { return Asn1ElementFactory.NAME; }

{BR_ROUND_OPEN}                                 { return Asn1CustomElementFactory.BRACES_ROUND_OPEN; }
{BR_ROUND_CLOSE}                                { return Asn1CustomElementFactory.BRACES_ROUND_CLOSE; }
{BR_CORNER_OPEN}                                { return Asn1CustomElementFactory.BRACES_CORNER_OPEN; }
{BR_CORNER_CLOSE}                               { return Asn1CustomElementFactory.BRACES_CORNER_CLOSE; }
{BR_CURLY_OPEN}                                 { return Asn1CustomElementFactory.BRACES_CURLY_OPEN; }
{BR_CURLY_CLOSE}                                { return Asn1CustomElementFactory.BRACES_CURLY_CLOSE; }

{CRLF}+                                         { return Asn1CustomElementFactory.LINE_BREAK; }
{WHITE_SPACE}+                                  { return Asn1CustomElementFactory.WHITE_SPACE; }
.                                               { return Asn1CustomElementFactory.BAD_CHARACTER; }