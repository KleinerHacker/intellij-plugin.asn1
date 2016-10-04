package org.pcsoft.plugin.intellij.asn1.type;

/**
 * Created by pfeifchr on 29.09.2016.
 */
public enum PrimitiveType {
    INTEGER("INTEGER", ValueType.INTEGER_NUMBER),
    BOOLEAN("BOOLEAN", ValueType.BOOLEAN),
    OCTET_STRING("OCTET STRING", ValueType.BYTES),
    PRINTABLE_STRING("PrintableString", ValueType.STRING),
    OBJECT_IDENTIFIER("OBJECT IDENTIFIER", ValueType.OBJECT_IDENTIFIER),
    NULL("NULL"),
    IA5_STRING("IA5String", ValueType.STRING),
    T61String("T61_STRING", ValueType.STRING),
    UTF8String("UTF8String", ValueType.STRING),
    TELETEX_STRING("TeleTexString", ValueType.STRING),
    GENERALIZED_TIME("GeneralizedTime", ValueType.DATE_TIME),
    UTC_TIME("UTCTime", ValueType.DATE_TIME),
    ;

    private final String name;
    private final ValueType[] valueTypes;

    private PrimitiveType(String name, ValueType... valueTypes) {
        this.name = name;
        this.valueTypes = valueTypes;
    }

    public ValueType[] getValueTypes() {
        return valueTypes;
    }

    public String getName() {
        return name;
    }
}
