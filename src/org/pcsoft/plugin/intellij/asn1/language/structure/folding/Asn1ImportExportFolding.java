package org.pcsoft.plugin.intellij.asn1.language.structure.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ExportDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ImportDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ImportElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pfeifchr on 29.09.2016.
 */
public class Asn1ImportExportFolding extends FoldingBuilderEx {

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement psiElement, @NotNull Document document, boolean b) {
        final List<FoldingDescriptor> list = new ArrayList<>();

        final Asn1ImportDefinition importDefinition = PsiTreeUtil.findChildOfType(psiElement, Asn1ImportDefinition.class);
        if (importDefinition != null) {
            if (importDefinition.getImportContent() != null && !importDefinition.getImportContent().getImportElementList().isEmpty()) {
                list.add(new FoldingDescriptor(importDefinition.getImportContent(), importDefinition.getImportContent().getTextRange()));
            }
        }

        final Asn1ExportDefinition exportDefinition = PsiTreeUtil.findChildOfType(psiElement, Asn1ExportDefinition.class);
        if (exportDefinition != null) {
            if (exportDefinition.getExportContent() != null && !exportDefinition.getExportContent().getImportExportSymbolList().isEmpty()) {
                list.add(new FoldingDescriptor(exportDefinition.getExportContent(), exportDefinition.getExportContent().getTextRange()));
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
        final Asn1ImportDefinition importDefinition = PsiTreeUtil.getParentOfType(astNode.getPsi(), Asn1ImportDefinition.class);
        if (importDefinition != null && importDefinition.getImportContent() != null) {
            int counterSymbol = 0, counterModule = 0;
            for (final Asn1ImportElement importElement : importDefinition.getImportContent().getImportElementList()) {
                counterModule++;
                counterSymbol += importElement.getImportExportSymbolList().size();
            }

            return counterSymbol + " symbol imported over " + counterModule + " modules";
        }

        final Asn1ExportDefinition exportDefinition = PsiTreeUtil.getParentOfType(astNode.getPsi(), Asn1ExportDefinition.class);
        if (exportDefinition != null && exportDefinition.getExportContent() != null) {
            return exportDefinition.getExportContent().getImportExportSymbolList().size() + " symbols exported";
        }

        return "...";
    }
}
