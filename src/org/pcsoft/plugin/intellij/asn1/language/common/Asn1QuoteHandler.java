package org.pcsoft.plugin.intellij.asn1.language.common;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;

/**
 * Created by pfeifchr on 28.09.2016.
 */
public class Asn1QuoteHandler extends SimpleTokenSetQuoteHandler {
    public Asn1QuoteHandler() {
        super(Asn1CustomElementFactory.QUOTE);
    }
}
