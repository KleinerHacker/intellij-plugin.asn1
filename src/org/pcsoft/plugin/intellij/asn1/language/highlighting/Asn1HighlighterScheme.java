package org.pcsoft.plugin.intellij.asn1.language.highlighting;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;

import java.awt.Font;

/**
 * Created by Christoph on 06.10.2016.
 */
public interface Asn1HighlighterScheme {
    TextAttributesKey OPERATOR = TextAttributesKey.createTextAttributesKey("ASN1_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    TextAttributesKey SEPARATOR = TextAttributesKey.createTextAttributesKey("ASN1_SEPARATOR", DefaultLanguageHighlighterColors.SEMICOLON);
    TextAttributesKey SYMBOL = TextAttributesKey.createTextAttributesKey("ASN1_SYMBOL", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    TextAttributesKey NAME = TextAttributesKey.createTextAttributesKey("ASN1_NAME", DefaultLanguageHighlighterColors.CLASS_NAME);
    TextAttributesKey TYPE = TextAttributesKey.createTextAttributesKey("ASN1_TYPE", DefaultLanguageHighlighterColors.CLASS_NAME);
    TextAttributesKey TYPE_REFERENCE = TextAttributesKey.createTextAttributesKey("ASN1_TYPE_REFERENCE", DefaultLanguageHighlighterColors.CLASS_REFERENCE);
    TextAttributesKey CLASS = TextAttributesKey.createTextAttributesKey("ASN1_CLASS", DefaultLanguageHighlighterColors.INTERFACE_NAME);
    TextAttributesKey CLASS_REFERENCE = TextAttributesKey.createTextAttributesKey("ASN1_CLASS_REFERENCE", DefaultLanguageHighlighterColors.CLASS_REFERENCE);
    TextAttributesKey ENUM = TextAttributesKey.createTextAttributesKey("ASN1_ENUM", DefaultLanguageHighlighterColors.CLASS_NAME);
    TextAttributesKey ENUM_REFERENCE = TextAttributesKey.createTextAttributesKey("ASN1_ENUM_REFERENCE", DefaultLanguageHighlighterColors.CLASS_REFERENCE);
    TextAttributesKey CHOICE = TextAttributesKey.createTextAttributesKey("ASN1_CHOICE", DefaultLanguageHighlighterColors.CLASS_NAME);
    TextAttributesKey CHOICE_REFERENCE = TextAttributesKey.createTextAttributesKey("ASN1_CHOICE_REFERENCE", DefaultLanguageHighlighterColors.CLASS_REFERENCE);
    TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey("ASN1_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    TextAttributesKey BAD_CHARACTER = TextAttributesKey.createTextAttributesKey("ASN1_NAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
    TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey("ASN1_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    TextAttributesKey NATIVE_TYPE = TextAttributesKey.createTextAttributesKey("ASN1_NATIVE_TYPE", DefaultLanguageHighlighterColors.KEYWORD);
    TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey("ASN1_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("ASN1_STRING", DefaultLanguageHighlighterColors.STRING);
    TextAttributesKey FIELD = TextAttributesKey.createTextAttributesKey("ASN1_FIELD", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    TextAttributesKey FIELD_REFERENCE = TextAttributesKey.createTextAttributesKey("ASN1_FIELD_REFERENCE", DefaultLanguageHighlighterColors.STATIC_FIELD);
    TextAttributesKey VALUE = TextAttributesKey.createTextAttributesKey("ASN1_VALUE", DefaultLanguageHighlighterColors.INSTANCE_METHOD);
    TextAttributesKey VALUE_REFERENCE = TextAttributesKey.createTextAttributesKey("ASN1_VALUE_REFERENCE", DefaultLanguageHighlighterColors.STATIC_METHOD);
    TextAttributesKey SET = TextAttributesKey.createTextAttributesKey("ASN1_SET", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    TextAttributesKey SET_REFERENCE = TextAttributesKey.createTextAttributesKey("ASN1_SET_REFERENCE", DefaultLanguageHighlighterColors.FUNCTION_CALL);
    TextAttributesKey PARAMETER = TextAttributesKey.createTextAttributesKey("ASN1_PARAMETER", DefaultLanguageHighlighterColors.PARAMETER);
    TextAttributesKey CONSTANT = TextAttributesKey.createTextAttributesKey("ASN1_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT);
    TextAttributesKey UNKNOWN_REFERENCE = TextAttributesKey.createTextAttributesKey("ASN1_UNKNOWN_REFERENCE", new TextAttributes(JBColor.RED, null, null, null, Font.BOLD));
}
