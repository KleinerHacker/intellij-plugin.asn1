package org.pcsoft.plugin.intellij.asn1.language.highlighting.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModifierDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModifierElement;

/**
 * Created by Christoph on 20.10.2016.
 */
public class Asn1ModifierAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof Asn1ModifierDefinition) {
            final Asn1ModifierDefinition modifierDefinition = (Asn1ModifierDefinition) psiElement;

            for (final Asn1ModifierElement modifierElement : modifierDefinition.getModifierElementList()) {
                modifierDefinition.getModifierElementList().stream()
                        .filter(item -> item.getModifier() == modifierElement.getModifier())
                        .filter(item -> item != modifierElement)
                        .forEach(item -> annotationHolder.createErrorAnnotation(item, "Modifier was already defined"));
            }
        }
    }
}
