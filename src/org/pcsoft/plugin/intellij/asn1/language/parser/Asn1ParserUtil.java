package org.pcsoft.plugin.intellij.asn1.language.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1GenElementFactory;

/**
 *
 */
public class Asn1ParserUtil extends GeneratedParserUtilBase {

    /**
     * Check that the following name is part of module identifier or of next import
     * @param builder
     * @param pos
     * @param oidParser
     * @return
     */
    public static boolean parseModuleRefOID(PsiBuilder builder, int pos, Parser oidParser) {
        //Is next element is a name
        if (builder.getTokenType() == Asn1GenElementFactory.NAME_CAP || builder.getTokenType() == Asn1GenElementFactory.NAME_LOWER ||
                builder.getTokenType() == Asn1GenElementFactory.NAME_NO_CAP || builder.getTokenType() == Asn1GenElementFactory.NAME_UPPER) {
            //create optional mark for value reference
            final PsiBuilder.Marker valueRefMark = builder.mark();
            builder.advanceLexer(); //goon

            //In case of keyword ('FROM') or ',' it is part of next import
            if (builder.getTokenType() == Asn1CustomElementFactory.KEYWORD || ",".equals(builder.getTokenText()) || builder.getTokenType() == Asn1CustomElementFactory.BRACES_CURLY_OPEN) {
                valueRefMark.rollbackTo(); // ergo roll back
                return true;
            }

            //it is a value reference
            valueRefMark.done(Asn1GenElementFactory.VALUE_REF);

            return true;
        } else if (builder.getTokenType() == Asn1CustomElementFactory.BRACES_CURLY_OPEN) {
            //Next is '{' it is object identifier, use parser
            return oidParser.parse(builder, pos);
        } else if (";".equals(builder.getTokenText())) {
            //It is end of import block
            return true;
        }

        return false; //Unknown place
    }

}
