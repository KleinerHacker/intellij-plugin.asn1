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
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1EnumeratedDefinitionElement;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionFieldName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionFieldRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectSetParameter;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectValueDefinitionName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectValueDefinitionRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TypeParameter;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ValueRef;

/**
 * Created by pfeifchr on 06.10.2016.
 */
public class Asn1FieldHighlightingAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof Asn1ClassDefinitionFieldName || psiElement instanceof Asn1ObjectClassDefinitionFieldName || psiElement instanceof Asn1EnumeratedDefinitionElement) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement.getNode(), null);
            infoAnnotation.setTextAttributes(Asn1HighlighterScheme.FIELD);
        } else if (psiElement instanceof Asn1ObjectClassDefinitionFieldRef || psiElement instanceof Asn1ClassDefinitionFieldRef) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement.getNode(), null);
            infoAnnotation.setTextAttributes(Asn1HighlighterScheme.FIELD_REFERENCE);
        } else if (psiElement instanceof Asn1ObjectValueDefinitionName) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement.getNode(), null);
            infoAnnotation.setTextAttributes(Asn1HighlighterScheme.OBJECT_VALUE);
        } else if (psiElement instanceof Asn1ObjectSetParameter || psiElement instanceof Asn1TypeParameter || psiElement instanceof Asn1ParameterRef) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement.getNode(), null);
            infoAnnotation.setTextAttributes(Asn1HighlighterScheme.PARAMETER);
        } else if (psiElement instanceof Asn1ConstantDefinitionValueName || psiElement instanceof Asn1ConstantDefinitionValueRef) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement.getNode(), null);
            infoAnnotation.setTextAttributes(Asn1HighlighterScheme.CONSTANT);
        } else if (psiElement instanceof Asn1ObjectValueDefinitionRef) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement.getNode(), null);
            infoAnnotation.setTextAttributes(Asn1HighlighterScheme.OBJECT_VALUE_REFERENCE);
        } else if (psiElement instanceof Asn1ValueRef) {
            final Asn1ValueRef valueRef = (Asn1ValueRef) psiElement;

            if (valueRef.getConstantReference().resolve() != null) {
                final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement.getNode(), null);
                infoAnnotation.setTextAttributes(Asn1HighlighterScheme.CONSTANT);
            } else if (valueRef.getObjectValueDefinitionReference().resolve() != null) {
                final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement.getNode(), null);
                infoAnnotation.setTextAttributes(Asn1HighlighterScheme.OBJECT_VALUE_REFERENCE);
            } else if (valueRef.getEnumeratedDefinitionElementReference().resolve() != null) {
                final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement.getNode(), null);
                infoAnnotation.setTextAttributes(Asn1HighlighterScheme.FIELD_REFERENCE);
            }
        }
    }
}