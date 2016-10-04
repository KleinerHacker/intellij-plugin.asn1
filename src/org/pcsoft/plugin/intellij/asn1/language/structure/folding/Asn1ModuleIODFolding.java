package org.pcsoft.plugin.intellij.asn1.language.structure.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pfeifchr on 29.09.2016.
 */
public class Asn1ModuleIODFolding extends FoldingBuilderEx {

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement psiElement, @NotNull Document document, boolean b) {
        final List<FoldingDescriptor> list = new ArrayList<>();

        final Asn1ModuleDefinition moduleDefinition = PsiTreeUtil.findChildOfType(psiElement, Asn1ModuleDefinition.class);
        if (moduleDefinition != null) {
            if (moduleDefinition.getValueObjectIdentifier() != null) {
                list.add(new FoldingDescriptor(moduleDefinition.getValueObjectIdentifier(), new TextRange(
                        moduleDefinition.getValueObjectIdentifier().getTextRange().getStartOffset() + 1,
                        moduleDefinition.getValueObjectIdentifier().getTextRange().getEndOffset() - 1
                )));
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
        return "OID";
    }
}
