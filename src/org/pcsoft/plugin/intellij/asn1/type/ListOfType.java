package org.pcsoft.plugin.intellij.asn1.type;

/**
 * Created by pfeifchr on 29.09.2016.
 */
public enum ListOfType {
    SEQUENCE("SEQUENCE OF"),
    SET("SET OF"),
    ;

    private final String name;

    private ListOfType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
