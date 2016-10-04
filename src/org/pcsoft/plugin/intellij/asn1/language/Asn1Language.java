package org.pcsoft.plugin.intellij.asn1.language;

import com.intellij.lang.Language;

/**
 *
 */
public class Asn1Language extends Language {
    public static final Asn1Language INSTANCE = new Asn1Language();

    private Asn1Language() {
        super("asn1");
    }
}
