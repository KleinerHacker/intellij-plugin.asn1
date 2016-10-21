package org.pcsoft.plugin.intellij.asn1.type;

import com.intellij.icons.AllIcons;
import com.intellij.psi.impl.ElementBase;

import javax.swing.Icon;

/**
 * Created by pfeifchr on 18.10.2016.
 */
public enum Asn1SymbolElement {
    TypeDefinition(AllIcons.Nodes.Class, Type.Assignment),
    ObjectClassDefinition(AllIcons.Nodes.Interface, Type.Assignment),
    ValueDefinition(AllIcons.Nodes.Variable, Type.Value),
    EnumeratedDefinition(AllIcons.Nodes.Enum, Type.Assignment),
    ChoiceDefinition(AllIcons.Nodes.Deploy, Type.Assignment),
    ObjectSetDefinition(ElementBase.buildRowIcon(AllIcons.Nodes.Advice, AllIcons.Nodes.AbstractClass), Type.Value),
    ValueSetDefinition(ElementBase.buildRowIcon(AllIcons.Nodes.Advice, AllIcons.Nodes.Variable), Type.Value),
    SetDefinition(AllIcons.Nodes.Advice, Type.Value),
    FieldTypeDefinition(AllIcons.Nodes.Function, Type.Value),
    CopyDefinition(AllIcons.Nodes.Annotationtype, Type.Inherited),
    ;

    public enum Type {
        Value,
        Assignment,
        Inherited
    }

    private final Icon icon;
    private final Type type;

    private Asn1SymbolElement(Icon icon, Type type) {
        this.icon = icon;
        this.type = type;
    }

    public Icon getIcon() {
        return icon;
    }

    public Type getType() {
        return type;
    }
}
