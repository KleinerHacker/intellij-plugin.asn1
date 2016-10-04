package org.pcsoft.plugin.intellij.asn1.language.structure.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.gen.Asn1ImportDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pfeifchr on 29.09.2016.
 */
public class Asn1ImportFolding extends FoldingBuilderEx {

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement psiElement, @NotNull Document document, boolean b) {
        final List<FoldingDescriptor> list = new ArrayList<>();

        final Asn1ImportDefinition importDefinition = PsiTreeUtil.findChildOfType(psiElement, Asn1ImportDefinition.class);
        if (importDefinition != null) {
            if (importDefinition.getImportContent() != null && importDefinition.getImportContent().getImportElementList().size() > 0) {
                list.add(new FoldingDescriptor(importDefinition.getImportContent(), importDefinition.getImportContent().getTextRange()));
            }
        }

        return list.toArray(new FoldingDescriptor[list.size()]);
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode astNode) {
        return true;
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode astNode) {
        return "...";
    }
}
