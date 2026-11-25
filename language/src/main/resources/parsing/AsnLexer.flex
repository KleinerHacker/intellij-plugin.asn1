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

package org.pcsoft.intellij.plugin.asn1.language.parser;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;

import static org.pcsoft.intellij.plugin.asn1.language.parser.psi.AsnTypes.*;

%%

%{
  public _AsnLexer() {
    this((java.io.Reader) null);
  }

  /**
   * Consumes the rest of a (possibly nested) block comment starting directly behind the opening delimiter.
   * The whole comment is returned as ONE token, so the lexer never needs a state beyond the initial one and the
   * editor may restart lexing at any token boundary. An unterminated comment runs to the end of the input.
   */
  private void skipBlockComment() {
    int depth = 1;
    int pos = zzMarkedPos;
    while (pos < zzEndRead && depth > 0) {
      char c = zzBuffer.charAt(pos);
      if (c == '/' && pos + 1 < zzEndRead && zzBuffer.charAt(pos + 1) == '*') {
        depth++;
        pos += 2;
      } else if (c == '*' && pos + 1 < zzEndRead && zzBuffer.charAt(pos + 1) == '/') {
        depth--;
        pos += 2;
      } else {
        pos++;
      }
    }
    zzMarkedPos = pos;
  }
%}

%public
%class _AsnLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

WHITE_SPACE     = [\ \t\r\n\f\u000B]+

NUMBER          = [0-9]+

// X.680 11.2 / 11.3: single hyphens only, no hyphen at the end, never two hyphens in a row
UPPER_REFERENCE = [A-Z] ("-"? [A-Za-z0-9])*
IDENTIFIER      = [a-z] ("-"? [A-Za-z0-9])*

// X.680 12.6: a line comment ends at the next "--" or at the end of the line
LINE_COMMENT    = "--" ([^\r\n\-] | "-" [^\r\n\-])* ("--" | "-")?

// All remaining ASCII punctuation. Needed by the (not yet parsed) module body, e.g. ',' ';' '[' ']' '|' '"'.
OTHER_SYMBOL    = [!-/:-@\[-`{-~]

%%

{WHITE_SPACE}      { return TokenType.WHITE_SPACE; }

{LINE_COMMENT}     { return LINE_COMMENT; }
"/*"               { skipBlockComment(); return BLOCK_COMMENT; }

"DEFINITIONS"      { return DEFINITIONS; }
"BEGIN"            { return BEGIN; }
"END"              { return END; }
"EXPLICIT"         { return EXPLICIT; }
"IMPLICIT"         { return IMPLICIT; }
"AUTOMATIC"        { return AUTOMATIC; }
"TAGS"             { return TAGS; }
"EXTENSIBILITY"    { return EXTENSIBILITY; }
"IMPLIED"          { return IMPLIED; }
"INSTRUCTIONS"     { return INSTRUCTIONS; }

"::="              { return ASSIGN; }
"::"               { return BAD_CHARACTER; }
"{"                { return LBRACE; }
"}"                { return RBRACE; }
"("                { return LPAREN; }
")"                { return RPAREN; }

{NUMBER}           { return NUMBER; }
{UPPER_REFERENCE}  { return UPPER_REFERENCE; }
{IDENTIFIER}       { return IDENTIFIER; }
{OTHER_SYMBOL}     { return OTHER_SYMBOL; }

// Catch-all: the lexer is total and never fails, whatever the input looks like.
[^]                { return BAD_CHARACTER; }
