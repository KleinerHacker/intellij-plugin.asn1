package org.pcsoft.plugin.intellij.asn1.language.highlighting.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolConstantElement;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolDefinition;
import org.pcsoft.plugin.intellij.asn1.type.Asn1SymbolElement;

/**
 * Created by Christoph on 20.10.2016.
 */
public class Asn1ConstantAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof Asn1SymbolConstantElement) {
            final Asn1SymbolConstantElement symbolConstantElement = (Asn1SymbolConstantElement) psiElement;
            if (symbolConstantElement.getSymbolConstantElementValue() == null) {
                final Asn1SymbolDefinition symbolDefinition = PsiTreeUtil.getParentOfType(symbolConstantElement, Asn1SymbolDefinition.class);
                if (symbolDefinition != null && symbolDefinition.getSymbolElement() != Asn1SymbolElement.EnumeratedDefinition) {
                    annotationHolder.createErrorAnnotation(psiElement, "Value is needed");
                }
            }
        }
    }
}
