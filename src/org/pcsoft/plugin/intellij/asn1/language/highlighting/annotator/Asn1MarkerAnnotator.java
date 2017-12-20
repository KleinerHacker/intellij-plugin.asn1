package org.pcsoft.plugin.intellij.asn1.language.highlighting.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.highlighting.Asn1HighlighterScheme;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterForSet;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterForSetIdentifier;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterForType;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterForTypeIdentifier;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolConstantElement;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolConstantIdentifier;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolFieldIdentifier;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolFieldReference;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolIdentifier;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolTypeReference;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolTypeReferenceValue;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ReferenceUtils;
import org.pcsoft.plugin.intellij.asn1.type.Asn1SymbolElement;

/**
 * Created by Christoph on 19.10.2016.
 */
public class Asn1MarkerAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof Asn1SymbolIdentifier) {
            final Asn1SymbolDefinition symbolDefinition = PsiTreeUtil.getParentOfType(psiElement, Asn1SymbolDefinition.class);
            if (symbolDefinition != null) {
                handleSymbolDefinition(psiElement, symbolDefinition, annotationHolder, false);
            } else {
                annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(Asn1HighlighterScheme.NAME);
            }
        } else if (psiElement instanceof Asn1SymbolFieldIdentifier) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(Asn1HighlighterScheme.FIELD);
        } else if (psiElement instanceof Asn1SymbolTypeReference) {
            final Asn1SymbolTypeReference symbolTypeReference = (Asn1SymbolTypeReference) psiElement;
            for (final Asn1SymbolTypeReferenceValue value : symbolTypeReference.getSymbolTypeReferenceValueList()) {
                final PsiElement element = Asn1ReferenceUtils.resolveFromMultiReference(value);
                if (element != null) {
                    if (element instanceof Asn1SymbolDefinition) {
                        handleSymbolDefinition(value, (Asn1SymbolDefinition) element, annotationHolder, true);
                    } else if (element instanceof Asn1SymbolDefinitionField) {
                        annotationHolder.createInfoAnnotation(value, null).setTextAttributes(Asn1HighlighterScheme.FIELD_REFERENCE);
                    } else if (element instanceof Asn1SymbolConstantElement) {
                        annotationHolder.createInfoAnnotation(value, null).setTextAttributes(Asn1HighlighterScheme.CONSTANT);
                    } else if (element instanceof Asn1ParameterForSet || element instanceof Asn1ParameterForType) {
                        annotationHolder.createInfoAnnotation(value, null).setTextAttributes(Asn1HighlighterScheme.PARAMETER);
                    }
                }
            }
        } else if (psiElement instanceof Asn1SymbolConstantIdentifier) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(Asn1HighlighterScheme.CONSTANT);
        } else if (psiElement instanceof Asn1ParameterForSetIdentifier || psiElement instanceof Asn1ParameterForTypeIdentifier) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(Asn1HighlighterScheme.PARAMETER);
        } else if (psiElement instanceof Asn1SymbolFieldReference) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(Asn1HighlighterScheme.FIELD_REFERENCE);
        }
    }

    private void handleSymbolDefinition(@NotNull PsiElement psiElement, Asn1SymbolDefinition symbolDefinition, @NotNull AnnotationHolder annotationHolder, boolean isReference) {
        final Asn1SymbolElement symbolElement = symbolDefinition.getSymbolElement();
        if (!handleSymbolElement(psiElement, symbolElement, annotationHolder, isReference)) {
            if (symbolElement == Asn1SymbolElement.CopyDefinition) {
                final Asn1SymbolElement symbolElementFromReferenceType = symbolDefinition.getSymbolElementFromReferenceType();
                handleSymbolElement(psiElement, symbolElementFromReferenceType, annotationHolder, isReference);
            }
        }
    }

    private boolean handleSymbolElement(@NotNull PsiElement psiElement, Asn1SymbolElement symbolElement, @NotNull AnnotationHolder annotationHolder, boolean isReference) {
        if (symbolElement == Asn1SymbolElement.ValueDefinition) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(
                    isReference ? Asn1HighlighterScheme.VALUE_REFERENCE : Asn1HighlighterScheme.VALUE
            );

            return true;
        } else if (symbolElement == Asn1SymbolElement.ValueSetDefinition || symbolElement == Asn1SymbolElement.ObjectSetDefinition) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(
                    isReference ? Asn1HighlighterScheme.SET_REFERENCE : Asn1HighlighterScheme.SET
            );

            return true;
        } else if (symbolElement == Asn1SymbolElement.TypeDefinition) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(
                    isReference ? Asn1HighlighterScheme.TYPE_REFERENCE : Asn1HighlighterScheme.TYPE
            );

            return true;
        } else if (symbolElement == Asn1SymbolElement.ObjectClassDefinition) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(
                    isReference ? Asn1HighlighterScheme.CLASS_REFERENCE : Asn1HighlighterScheme.CLASS
            );

            return true;
        }

        return false;
    }
}
