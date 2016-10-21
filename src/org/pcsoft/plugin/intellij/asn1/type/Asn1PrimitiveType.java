package org.pcsoft.plugin.intellij.asn1.type;

/**
 * Created by pfeifchr on 29.09.2016.
 */
public enum Asn1PrimitiveType {
    INTEGER("INTEGER", Asn1ValueType.INTEGER_NUMBER),
    BOOLEAN("BOOLEAN", Asn1ValueType.BOOLEAN),
    OCTET_STRING("OCTET STRING", Asn1ValueType.BYTES),
    PRINTABLE_STRING("PrintableString", Asn1ValueType.STRING),
    OBJECT_IDENTIFIER("OBJECT IDENTIFIER", Asn1ValueType.OBJECT_IDENTIFIER),
    NULL("NULL"),
    IA5_STRING("IA5String", Asn1ValueType.STRING),
    T61String("T61_STRING", Asn1ValueType.STRING),
    UTF8String("UTF8String", Asn1ValueType.STRING),
    NumericString("NumericString", Asn1ValueType.STRING),
    VIDEOTEX_STRING("VideoTexString", Asn1ValueType.STRING),
    GENERALIZED_TIME("GeneralizedTime", Asn1ValueType.DATE_TIME),
    UTC_TIME("UTCTime", Asn1ValueType.DATE_TIME),
    ANY("ANY", Asn1ValueType.VALUE, Asn1ValueType.BOOLEAN, Asn1ValueType.BYTES, Asn1ValueType.DATE_TIME, Asn1ValueType.INTEGER_NUMBER, Asn1ValueType.OBJECT_IDENTIFIER, Asn1ValueType.STRING)
    ;

    private final String name;
    private final Asn1ValueType[] valueTypes;

    private Asn1PrimitiveType(String name, Asn1ValueType... valueTypes) {
        this.name = name;
        this.valueTypes = valueTypes;
    }

    public Asn1ValueType[] getValueTypes() {
        return valueTypes;
    }

    public String getName() {
        return name;
    }
}
