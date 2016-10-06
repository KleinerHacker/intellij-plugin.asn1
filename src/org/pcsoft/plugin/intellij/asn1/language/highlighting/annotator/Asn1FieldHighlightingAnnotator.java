package org.pcsoft.plugin.intellij.asn1.language.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.highlighting.Asn1HighlighterScheme;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ClassDefinitionFieldName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ClassDefinitionFieldRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ConstantDefinitionValueName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ConstantDefinitionValueRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionFieldName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionFieldRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ValueRef;

/**
 * Created by pfeifchr on 06.10.2016.
 */
public class Asn1FieldHighlightingAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof Asn1ClassDefinitionFieldName || psiElement instanceof Asn1ObjectClassDefinitionFieldName) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement.getNode(), null);
            infoAnnotation.setTextAttributes(Asn1HighlighterScheme.FIELD);
        } else if (psiElement instanceof Asn1ObjectClassDefinitionFieldRef || psiElement instanceof Asn1ClassDefinitionFieldRef) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement.getNode(), null);
            infoAnnotation.setTextAttributes(Asn1HighlighterScheme.FIELD_REFERENCE);
        } else if (psiElement instanceof Asn1ParameterName || psiElement instanceof Asn1ParameterRef) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement.getNode(), null);
            infoAnnotation.setTextAttributes(Asn1HighlighterScheme.PARAMETER);
        } else if (psiElement instanceof Asn1ConstantDefinitionValueName || psiElement instanceof Asn1ConstantDefinitionValueRef) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement.getNode(), null);
            infoAnnotation.setTextAttributes(Asn1HighlighterScheme.CONSTANT);
        } else if (psiElement instanceof Asn1ValueRef) {
            if (((Asn1ValueRef) psiElement).getConstantReference().resolve() != null) {
                final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement.getNode(), null);
                infoAnnotation.setTextAttributes(Asn1HighlighterScheme.CONSTANT);
            }
        }
    }
}
