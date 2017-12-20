package org.pcsoft.plugin.intellij.asn1.language.highlighting.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleIdentifier;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolValueString;

/**
 * Created by Christoph on 13.10.2016.
 */
public class Asn1ModuleIdentifierAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof Asn1SymbolValueString) {
            final Asn1ModuleIdentifier moduleIdentifier = PsiTreeUtil.getParentOfType(psiElement, Asn1ModuleIdentifier.class);

            if (moduleIdentifier != null && moduleIdentifier.getSymbolValueString() != null) {
                if (!moduleIdentifier.getSymbolValueString().getString().matches("(/[^/]+)+")) {
                    annotationHolder.createErrorAnnotation(moduleIdentifier.getSymbolValueString(), "Wrong IRI format");
                }
            }
        }
    }
}
