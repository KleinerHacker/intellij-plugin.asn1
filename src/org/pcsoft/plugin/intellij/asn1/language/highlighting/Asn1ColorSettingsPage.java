package org.pcsoft.plugin.intellij.asn1.language.highlighting;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugin.intellij.asn1.Asn1Icons;
import org.pcsoft.plugin.intellij.asn1.language.highlighting.syntax.Asn1SyntaxHighlighter;

import javax.swing.Icon;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pfeifchr on 06.10.2016.
 */
public class Asn1ColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Choice", Asn1HighlighterScheme.CHOICE),
            new AttributesDescriptor("Choice Reference", Asn1HighlighterScheme.CHOICE_REFERENCE),
            new AttributesDescriptor("Comment", Asn1HighlighterScheme.COMMENT),
            new AttributesDescriptor("Common Name", Asn1HighlighterScheme.NAME),
            new AttributesDescriptor("Constant", Asn1HighlighterScheme.CONSTANT),
            new AttributesDescriptor("Enumerated", Asn1HighlighterScheme.ENUM),
            new AttributesDescriptor("Enumerated Reference", Asn1HighlighterScheme.ENUM_REFERENCE),
            new AttributesDescriptor("Field Reference", Asn1HighlighterScheme.FIELD_REFERENCE),
            new AttributesDescriptor("Field", Asn1HighlighterScheme.FIELD),
            new AttributesDescriptor("Keyword", Asn1HighlighterScheme.KEYWORD),
            new AttributesDescriptor("Number Value", Asn1HighlighterScheme.NUMBER),
            new AttributesDescriptor("Value", Asn1HighlighterScheme.VALUE),
            new AttributesDescriptor("Value Reference", Asn1HighlighterScheme.VALUE_REFERENCE),
            new AttributesDescriptor("Object Class", Asn1HighlighterScheme.CLASS),
            new AttributesDescriptor("Object Class Reference", Asn1HighlighterScheme.CLASS_REFERENCE),
            new AttributesDescriptor("Operators", Asn1HighlighterScheme.OPERATOR),
            new AttributesDescriptor("Parameter", Asn1HighlighterScheme.PARAMETER),
            new AttributesDescriptor("Separators", Asn1HighlighterScheme.SEPARATOR),
            new AttributesDescriptor("String Value", Asn1HighlighterScheme.STRING),
            new AttributesDescriptor("Set", Asn1HighlighterScheme.SET),
            new AttributesDescriptor("Set Reference", Asn1HighlighterScheme.SET_REFERENCE),
            new AttributesDescriptor("Symbols", Asn1HighlighterScheme.SYMBOL),
            new AttributesDescriptor("Type", Asn1HighlighterScheme.TYPE),
            new AttributesDescriptor("Type Reference", Asn1HighlighterScheme.TYPE_REFERENCE),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return Asn1Icons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new Asn1SyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "-- Comment\n" +
                "MyModule {iso(1) bla(3) 4 <VALUE_REF>myObject1</VALUE_REF>} DEFINITIONS EXPLICIT TAGS ::=\n" +
                "BEGIN --vgui\n" +
                "\n" +
                "IMPORTS\n" +
                "<TYPE_REF>MyBla</TYPE_REF>, <TYPE_REF>MyBlub</TYPE_REF> FROM MyOtherModule {iso(4) 4 2 3} <VALUE_REF>myObject2</VALUE_REF>\n" +
                ";\n" +
                "\n" +
                "<TYPE>MyClass1</TYPE> ::= INTEGER\n" +
                "<TYPE>MyClass2</TYPE> ::= [10] SEQUENCE {\n" +
                "    <FIELD>myValue1</FIELD> [APPLICATION 10] INTEGER {<CONST>val</CONST>(0), <CONST>val2</CONST>(1)} DEFAULT <CONST>val</CONST>,\n" +
                "    <FIELD>myValue2</FIELD> SET OF BOOLEAN {<CONST>on</CONST>(TRUE)}\n" +
                "    [[2:\n" +
                "        <FIELD>verField1</FIELD> <ENUM_REF>MyEnum1</ENUM_REF>,\n" +
                "        <FIELD>verField2</FIELD> <CHOICE_REF>MyChoice1</CHOICE_REF>\n" +
                "    ]],\n" +
                "    <FIELD>myValue3</FIELD> CHOICE {\n" +
                "        <FIELD>this</FIELD> [0] MyClass4{<CLASS_REF>MY_OBJ_CLASS_1</CLASS_REF>,{<SET_REF>MyObjectSet1</SET_REF>},{<SET_REF>MyObjectSet1</SET_REF>}},\n" +
                "        <FIELD>taht</FIELD> [1] <TYPE_REF>MyClass1</TYPE_REF>\n" +
                "    }\n" +
                "}\n" +
                "<TYPE>MyClass3</TYPE> ::= <TYPE_REF>MyClass2</TYPE_REF>\n" +
                "<TYPE>MyClass4</TYPE> {<PARAM>Param</PARAM>,<PARAM>Param</PARAM>:<PARAM>Obj1</PARAM>,<CLASS_REF>MY_OBJ_CLASS_1</CLASS_REF>:<PARAM>Obj2</PARAM>} ::= SEQUENCE {\n" +
                "    <FIELD>myValue1</FIELD> <CLASS_REF>MY_OBJ_CLASS_1</CLASS_REF>.&<FIELD_REF>myValue1</FIELD_REF>({<PARAM>Obj2</PARAM>}),\n" +
                "    <FIELD>myValue2</FIELD> <CLASS_REF>MY_OBJ_CLASS_2</CLASS_REF>.&<FIELD_REF>myValue2</FIELD_REF>({<PARAM>Obj2</PARAM>}{@<FIELD_REF>myValue1</FIELD_REF>}),\n" +
                "    <FIELD>myValue3</FIELD> <TYPE_REF>MyClass5</TYPE_REF>{{<PARAM>Obj1</PARAM>}}\n" +
                "}\n" +
                "<TYPE>MyClass5</TYPE> {<CLASS_REF>MY_OBJ_CLASS_1</CLASS_REF>:<PARAM>Param</PARAM>} ::= SEQUENCE {\n" +
                "    <FIELD>myValue1</FIELD> INTEGER,\n" +
                "    <FIELD>myValue2</FIELD> <CLASS_REF>MY_OBJ_CLASS_1</CLASS_REF>.&<FIELD_REF>myValue1</FIELD_REF>({<PARAM>Param</PARAM>}{@<FIELD_REF>myValue1</FIELD_REF>})\n" +
                "}\n" +
                "\n" +
                "<CLASS>MY_OBJ_CLASS_1</CLASS> ::= CLASS {\n" +
                "    &<FIELD>myValue1</FIELD> INTEGER,\n" +
                "    &<FIELD>myValue2</FIELD> OPTIONAL\n" +
                "} WITH SYNTAX {\n" +
                "    INDENTIFIED BY &<FIELD_REF>myValue1</FIELD_REF>\n" +
                "}\n" +
                "\n" +
                "<CLASS>MY_OBJ_CLASS_2</CLASS> ::= CLASS {\n" +
                "    &<FIELD>myValue1</FIELD> BOOLEAN,\n" +
                "    &<FIELD>myValue2</FIELD> OPTIONAL\n" +
                "}\n" +
                "\n" +
                "<VALUE>myObject1</VALUE> INTEGER ::= 1\n" +
                "<VALUE>myObject2</VALUE> OBJECT IDENTIFIER ::= {iso(4) 3 2 1}\n" +
                "<VALUE>myObject3</VALUE> <TYPE_REF>MyClass2</TYPE_REF> ::= {\n" +
                "    <FIELD_REF>myValue1</FIELD_REF> <VALUE_REF>myObject1</VALUE_REF>,\n" +
                "    <FIELD_REF>myValue2</FIELD_REF> <CONST>on</CONST>\n" +
                "}\n" +
                "<VALUE>myObject4</VALUE> <CLASS_REF>MY_OBJ_CLASS_1</CLASS_REF> ::= {IDENTIFIED BY <VALUE_REF>myObject1</VALUE_REF>}\n" +
                "<VALUE>myObject5</VALUE> UTF8String ::= \"abc\"\n" +
                "\n" +
                "<SET>MyValueSet1</SET> INTEGER ::= {10, 20, 30}\n" +
                "<SET>MyObjectSet1</SET> <CLASS_REF>MY_OBJ_CLASS_1</CLASS_REF> ::= {\n" +
                "    {IDENTIFIED BY <VALUE_REF>myObject1</VALUE_REF>}\n" +
                "}\n" +
                "\n" +
                "<CHOICE>MyChoice1</CHOICE> ::= CHOICE {\n" +
                "    <FIELD>choiceValue1</FIELD> [0] INTEGER,\n" +
                "    <FIELD>choiceValue2</FIELD> [1] <TYPE_REF>MyClass1</TYPE_REF>\n" +
                "}\n" +
                "\n" +
                "<ENUM>MyEnum1</ENUM> ::= ENUMERATED {\n" +
                "    <CONST>myConst1</CONST>,\n" +
                "    <CONST>MyConst2</CONST>\n" +
                "}\n" +
                "\n" +
                "END";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        final Map<String, TextAttributesKey> map = new HashMap<>();
        map.put("FIELD", Asn1HighlighterScheme.FIELD);
        map.put("FIELD_REF", Asn1HighlighterScheme.FIELD_REFERENCE);
        map.put("VALUE", Asn1HighlighterScheme.VALUE);
        map.put("VALUE_REF", Asn1HighlighterScheme.VALUE_REFERENCE);
        map.put("SET", Asn1HighlighterScheme.SET);
        map.put("SET_REF", Asn1HighlighterScheme.SET_REFERENCE);
        map.put("TYPE", Asn1HighlighterScheme.TYPE);
        map.put("TYPE_REF", Asn1HighlighterScheme.TYPE_REFERENCE);
        map.put("CLASS", Asn1HighlighterScheme.CLASS);
        map.put("CLASS_REF", Asn1HighlighterScheme.CLASS_REFERENCE);
        map.put("ENUM", Asn1HighlighterScheme.ENUM);
        map.put("ENUM_REF", Asn1HighlighterScheme.ENUM_REFERENCE);
        map.put("CHOICE", Asn1HighlighterScheme.CHOICE);
        map.put("CHOICE_REF", Asn1HighlighterScheme.CHOICE_REFERENCE);
        map.put("PARAM", Asn1HighlighterScheme.PARAMETER);
        map.put("CONST", Asn1HighlighterScheme.CONSTANT);

        return map;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "ASN1";
    }
}
