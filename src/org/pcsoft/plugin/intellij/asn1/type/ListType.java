package org.pcsoft.plugin.intellij.asn1.type;

/**
 * Created by pfeifchr on 29.09.2016.
 */
public enum ListType {
    SEQUENCE("SEQUENCE"),
    SET("SET"),
    ;

    private final String name;

    private ListType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
