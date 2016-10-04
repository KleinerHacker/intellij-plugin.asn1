package org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;

public abstract class Asn1ElementManipulator<T extends PsiNamedElement> implements ElementManipulator<T> {

    @Override
    public TextRange getRangeInElement(T element) {
        return new TextRange(0, element.getName().length());
    }

    @Override
    public T handleContentChange(T element, TextRange textRange, String s) throws IncorrectOperationException {
        return (T) element.setName(s);
    }

    @Override
    public T handleContentChange(T element, String s) throws IncorrectOperationException {
        return (T) element.setName(s);
    }
}