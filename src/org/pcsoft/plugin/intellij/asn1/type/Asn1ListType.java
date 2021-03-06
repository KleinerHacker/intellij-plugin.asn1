package org.pcsoft.plugin.intellij.asn1.type;

/**
 * Created by Christoph on 29.09.2016.
 */
public enum Asn1ListType {
    SEQUENCE("SEQUENCE"),
    SET("SET"),
    CHOICE("CHOICE")
    ;

    private final String name;

    private Asn1ListType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
