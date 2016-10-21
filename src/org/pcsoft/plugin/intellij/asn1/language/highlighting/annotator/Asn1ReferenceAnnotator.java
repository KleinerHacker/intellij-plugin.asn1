package org.pcsoft.plugin.intellij.asn1.language.highlighting.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.highlighting.Asn1HighlighterScheme;

/**
 * Created by pfeifchr on 06.10.2016.
 */
public class Asn1ReferenceAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement.getReference() != null) {
            //final Asn1ObjectClassDefinitionConstructorCall constructorCall = PsiTreeUtil.getParentOfType(psiElement, Asn1ObjectClassDefinitionConstructorCall.class);
            //if (constructorCall == null) {
                if (psiElement.getReference().resolve() == null) {
                    annotationHolder.createErrorAnnotation(psiElement.getNode(), "unable to find a reference")
                            .setTextAttributes(Asn1HighlighterScheme.UNKNOWN_REFERENCE);
                }
            //}
        } else if (psiElement.getReferences().length > 0) {
            //final Asn1ObjectClassDefinitionConstructorCall constructorCall = PsiTreeUtil.getParentOfType(psiElement, Asn1ObjectClassDefinitionConstructorCall.class);
            //if (constructorCall == null) {
                boolean foundReference = false;
                for (final PsiReference reference : psiElement.getReferences()) {
                    if (reference.resolve() != null) {
                        foundReference = true;
                        break;
                    }
                }
                if (!foundReference) {
                    annotationHolder.createErrorAnnotation(psiElement.getNode(), "unable to find a reference")
                            .setTextAttributes(Asn1HighlighterScheme.UNKNOWN_REFERENCE);
                }
            //}
        }
    }
}
