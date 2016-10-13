package org.pcsoft.plugin.intellij.asn1.language.parser.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1GenElementFactory;
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
COMMENT_LINE="--".*("--"|{CRLF})
COMMENT_BLOCK="/*".*"*/"
NUMBER=[\-]?[0-9]*
TEXT=[^\"]*

NAME_UPPER=[A-Z]{1}[A-Z0-9_\-]*
NAME_LOWER=[a-z]{1}[a-z0-9_\-]*
NAME_CAP=[A-Z]{1}[A-Za-z0-9_\-]*
NAME_NO_CAP=[a-z]{1}[A-Za-z0-9_\-]*

OPERATOR= "::="
SEPARATOR="," | ";" | ":" | "." | "|"
SYMBOL="&" | "@"
QUOTE=\"

BR_ROUND_OPEN="("
BR_ROUND_CLOSE=")"
BR_CURLY_OPEN="{"
BR_CURLY_CLOSE="}"
BR_CORNER_OPEN="["
BR_CORNER_CLOSE="]"

KEYWORD="DEFINITIONS" | "IMPLICIT" | "EXPLICIT" | "AUTOMATIC" | "TAGS" | "BEGIN" | "END" | "APPLICATION" | "IMPORTS" | "FROM" | "CLASS" | "UNIQUE" | "OPTIONAL" |
"TRUE" | "FALSE" | "DEFAULT" | "WITH SYNTAX" | "MIN" | "MAX" | "SIZE" | "OF" | "DEFINED BY" | "ANY" | "EXPORTS" | "ALL" | "WITH COMPONENTS" | "PRESENT" | "ABSENT" |
"INSTANCE OF" | "INSTRUCTIONS" | "EXTENSIBILITY IMPLIED" | "ENCODING-CONTROL"
PRIMITIVE_TYPE="INTEGER" | ("BIT" {WHITE_SPACE}+ "STRING") | "BOOLEAN" | ("OCTET" {WHITE_SPACE}+ "STRING") | ("OBJECT" {WHITE_SPACE}+ "IDENTIFIER") |
 "PrintableString" | "IA5String" | "UTF8String" | "T61String" | "GeneralizedTime" | "UTCTime" | "VideoTexString" | "NumericString" | "TeletexString" | "NULL"
LIST_TYPE="SEQUENCE" | "SET" | "CHOICE" | "ENUMERATED"

%%

<YYINITIAL> {
    {COMMENT_LINE}                                  { return Asn1CustomElementFactory.COMMENT; }
    {COMMENT_BLOCK}                                 { return Asn1CustomElementFactory.COMMENT; }

    {KEYWORD}                                       { return Asn1CustomElementFactory.KEYWORD; }
    {PRIMITIVE_TYPE}                                { return Asn1GenElementFactory.PRIMITIVE_TYPE; }
    {LIST_TYPE}                                     { return Asn1GenElementFactory.LIST_TYPE; }
    {OPERATOR}                                      { return Asn1CustomElementFactory.OPERATOR; }
    {SEPARATOR}                                     { return Asn1CustomElementFactory.SEPARATOR; }
    {SYMBOL}                                        { return Asn1CustomElementFactory.SYMBOL; }
    <STRING> {
        {QUOTE}                                     { if (yystate() == STRING) yybegin(YYINITIAL); else yybegin(STRING); return Asn1CustomElementFactory.QUOTE; }
    }

    {NUMBER}                                        { return Asn1GenElementFactory.NUMBER; }


    {NAME_UPPER}                                    { return Asn1GenElementFactory.NAME_UPPER; }
    {NAME_LOWER}                                    { return Asn1GenElementFactory.NAME_LOWER; }
    {NAME_CAP}                                      { return Asn1GenElementFactory.NAME_CAP; }
    {NAME_NO_CAP}                                   { return Asn1GenElementFactory.NAME_NO_CAP; }

    {BR_ROUND_OPEN}                                 { return Asn1CustomElementFactory.BRACES_ROUND_OPEN; }
    {BR_ROUND_CLOSE}                                { return Asn1CustomElementFactory.BRACES_ROUND_CLOSE; }
    {BR_CORNER_OPEN}                                { return Asn1CustomElementFactory.BRACES_CORNER_OPEN; }
    {BR_CORNER_CLOSE}                               { return Asn1CustomElementFactory.BRACES_CORNER_CLOSE; }
    {BR_CURLY_OPEN}                                 { return Asn1CustomElementFactory.BRACES_CURLY_OPEN; }
    {BR_CURLY_CLOSE}                                { return Asn1CustomElementFactory.BRACES_CURLY_CLOSE; }

    {CRLF}+                                         { return Asn1CustomElementFactory.LINE_BREAK; }
    {WHITE_SPACE}+                                  { return Asn1CustomElementFactory.WHITE_SPACE; }
}

<STRING> {
    {TEXT}                                      { return Asn1GenElementFactory.TEXT; }
}

.                                               { return Asn1CustomElementFactory.BAD_CHARACTER; }