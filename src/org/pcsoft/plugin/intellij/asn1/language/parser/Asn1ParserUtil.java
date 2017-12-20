package org.pcsoft.plugin.intellij.asn1.language.parser;

import com.intellij.lang.LighterASTNode;
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
     *
     * @param builder
     * @param pos
     * @param oidParser
     * @return
     */
    public static boolean parseModuleRefOID(PsiBuilder builder, int pos, Parser oidParser) {
        //Is next element is a name
        if (builder.getTokenType() == Asn1GenElementFactory.NAME) {
            //create optional mark for reference
            final PsiBuilder.Marker valueRefMark = builder.mark();
            builder.advanceLexer(); //goon

            //In case of keyword ('FROM') or ',' it is part of next import
            if (builder.getTokenType() == Asn1CustomElementFactory.KEYWORD || ",".equals(builder.getTokenText()) || builder.getTokenType() == Asn1CustomElementFactory.BRACES_CURLY_OPEN) {
                valueRefMark.rollbackTo(); // ergo roll back
                return true;
            }

            //it is a reference
            valueRefMark.done(Asn1GenElementFactory.SYMBOL_TYPE_REFERENCE);

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

    /**
     * Parse symbol definition based on case sensitive of name
     *
     * @param builder
     * @param pos
     * @param identifierParser
     * @param parameterParser
     * @param identifierTypeParser
     * @param assignmentParser
     * @param typeParser
     * @param valueParser
     * @param valueSetParser
     * @return
     */
    public static boolean parseSymbolDefinition(PsiBuilder builder, int pos, Parser identifierParser, Parser parameterParser, Parser identifierTypeParser, Parser assignmentParser, Parser typeParser, Parser valueParser, Parser valueSetParser) {
        if (!identifierParser.parse(builder, pos))
            return false;

        final LighterASTNode latestIdentifierMarker = builder.getLatestDoneMarker();
        if (latestIdentifierMarker == null)
            return false;

        final CharSequence identifier = builder.getOriginalText().subSequence(latestIdentifierMarker.getStartOffset(), latestIdentifierMarker.getEndOffset());

        if (!parameterParser.parse(builder, pos))
            return false;

        final LighterASTNode parameterMarker = builder.getLatestDoneMarker();

        if (!identifierTypeParser.parse(builder, pos))
            return false;

        final LighterASTNode latestTypeMarker = builder.getLatestDoneMarker();
        final boolean hasIdentifierType = latestTypeMarker != null && latestTypeMarker != parameterMarker;
        //final boolean objectIdentifierMark = hasIdentifierType && builder.getOriginalText().subSequence(latestTypeMarker.getStartOffset(), latestTypeMarker.getEndOffset()).toString().matches("OBJECT[\\s]+IDENTIFIER");

        if (!assignmentParser.parse(builder, pos))
            return false;

        if (identifier.charAt(0) == Character.toUpperCase(identifier.charAt(0))) {
            if (hasIdentifierType) {
                if (!valueSetParser.parse(builder, pos))
                    return false;
            } else {
                if (!typeParser.parse(builder, pos))
                    return false;
            }
        } else {
            if (!valueParser.parse(builder, pos))
                return false;
        }

        return true;
    }

}
