package org.pcsoft.plugin.intellij.asn1.language.parser.lexer;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

/**
 *
 */
public class Asn1LexerAdapter extends FlexAdapter {
    public Asn1LexerAdapter() {
        super(new Asn1Lexer((Reader) null));
    }
}
