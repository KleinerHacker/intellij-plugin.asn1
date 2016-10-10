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
import java.util.Map;

/**
 * Created by pfeifchr on 06.10.2016.
 */
public class Asn1ColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[] {
            new AttributesDescriptor("COMMENT", Asn1HighlighterScheme.COMMENT),
            new AttributesDescriptor("CONSTANT", Asn1HighlighterScheme.CONSTANT),
            new AttributesDescriptor("FIELD REFERENCE", Asn1HighlighterScheme.FIELD_REFERENCE),
            new AttributesDescriptor("FIELD", Asn1HighlighterScheme.FIELD),
            new AttributesDescriptor("KEYWORD", Asn1HighlighterScheme.KEYWORD),
            new AttributesDescriptor("NAME", Asn1HighlighterScheme.NAME),
            new AttributesDescriptor("NUMBER", Asn1HighlighterScheme.NUMBER),
            new AttributesDescriptor("OBJECT VALUE", Asn1HighlighterScheme.OBJECT_VALUE),
            new AttributesDescriptor("OBJECT VALUE REFERENCE", Asn1HighlighterScheme.OBJECT_VALUE_REFERENCE),
            new AttributesDescriptor("OPERATOR", Asn1HighlighterScheme.OPERATOR),
            new AttributesDescriptor("PARAMETER", Asn1HighlighterScheme.PARAMETER),
            new AttributesDescriptor("SEPARATOR", Asn1HighlighterScheme.SEPARATOR),
            new AttributesDescriptor("STRING", Asn1HighlighterScheme.STRING),
            new AttributesDescriptor("SYMBOL", Asn1HighlighterScheme.SYMBOL),
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
                "MyModule {iso(1) bla(3) 4 name} DEFINITIONS EXPLICIT TAGS ::=\n" +
                "BEGIN --vgui\n" +
                "\n" +
                "IMPORTS\n" +
                "MyBla, MyBlub FROM MyOtherModule {iso(4) 4 2 3} test\n" +
                ";\n" +
                "\n" +
                "MyClass1 ::= INTEGER\n" +
                "MyClass2 ::= [10] SEQUENCE {\n" +
                "    myValue1 [APPLICATION 10] INTEGER {val(0), val2(1)} DEFAULT val,\n" +
                "    myValue2 SET OF BOOLEAN {on(TRUE)}\n" +
                "}\n" +
                "MyClass3 ::= MyClass2\n" +
                "MyClass4 {MY_OBJ_CLASS_1:Obj1,MY_OBJ_CLASS_2:Obj2} ::= SEQUENCE {\n" +
                "    myValue1 MY_OBJ_CLASS_1.&myValue1({Obj1}),\n" +
                "    myValue2 MY_OBJ_CLASS_2.&myValue2({Obj2}{@myValue1}),\n" +
                "    myValue3 MyClass5{{Obj1}}\n" +
                "}\n" +
                "MyClass5 {MY_OBJ_CLASS_1:Param} ::= SEQUENCE {\n" +
                "    myValue1 INTEGER,\n" +
                "    myValue2 MY_OBJ_CLASS_1.&myValue1({Param}{@myValue1})\n" +
                "}\n" +
                "\n" +
                "MY_OBJ_CLASS_1 ::= CLASS {\n" +
                "    &myValue1 INTEGER,\n" +
                "    &myValue2 OPTIONAL\n" +
                "} WITH SYNTAX {\n" +
                "    CREATE INDENTIFIED BY\n" +
                "}\n" +
                "\n" +
                "MY_OBJ_CLASS_2 ::= CLASS {\n" +
                "    &myValue1 BOOLEAN,\n" +
                "    &myValue2 OPTIONAL\n" +
                "}\n" +
                "\n" +
                "myObject1 INTEGER ::= 1\n" +
                "myObject2 OBJECT IDENTIFIER ::= {iso(4) 3 2 1}\n" +
                "myObject3 MyClass2 ::= {\n" +
                "    myValue1 myObject1,\n" +
                "    myValue2 on\n" +
                "}\n" +
                "myObject4 MY_OBJ_CLASS_1 ::= {START any A}\n" +
                "myObject5 MyClass2 ::= {\n" +
                "    myValue1 \"abc\",\n" +
                "    myValue2 myObject2\n" +
                "}\n" +
                "\n" +
                "END";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
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
