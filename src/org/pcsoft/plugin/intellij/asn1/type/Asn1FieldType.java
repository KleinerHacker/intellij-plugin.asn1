package org.pcsoft.plugin.intellij.asn1.type;

import com.intellij.icons.AllIcons;
import com.intellij.psi.impl.ElementBase;

import javax.swing.Icon;

/**
 * Created by pfeifchr on 18.10.2016.
 */
public enum Asn1FieldType {
    TypeField(AllIcons.Nodes.Field),
    ObjectClassField(ElementBase.overlayIcons(AllIcons.Nodes.Field, AllIcons.Nodes.StaticMark)),
    ;

    private final Icon icon;

    private Asn1FieldType(Icon icon) {
        this.icon = icon;
    }

    public Icon getIcon() {
        return icon;
    }
}
