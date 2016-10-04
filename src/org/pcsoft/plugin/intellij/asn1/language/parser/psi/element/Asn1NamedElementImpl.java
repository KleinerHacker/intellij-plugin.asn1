package org.pcsoft.plugin.intellij.asn1.language.parser.psi.element;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public abstract class Asn1NamedElementImpl extends ASTWrapperPsiElement implements Asn1NamedElement {
    public Asn1NamedElementImpl(@NotNull ASTNode astNode) {
        super(astNode);
    }
}
