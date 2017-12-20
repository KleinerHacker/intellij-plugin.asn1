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
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christoph on 29.09.2016.
 */
public class Asn1SymbolFolding extends FoldingBuilderEx {

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement psiElement, @NotNull Document document, boolean b) {
        final List<FoldingDescriptor> list = new ArrayList<>();

        final Asn1ModuleDefinition moduleDefinition = PsiTreeUtil.findChildOfType(psiElement, Asn1ModuleDefinition.class);
        if (moduleDefinition != null && moduleDefinition.getModuleContent() != null) {
            for (final Asn1SymbolDefinition symbolDefinition : moduleDefinition.getModuleContent().getSymbolDefinitionList()) {
                if (symbolDefinition.getSymbolContent() != null) {
                    if (symbolDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN) != null &&
                            symbolDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE) != null) {

                        final TextRange textRange = new TextRange(
                                symbolDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                symbolDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                        );
                        if (!textRange.isEmpty()) {
                            list.add(new FoldingDescriptor(symbolDefinition.getSymbolContent(), textRange));
                        }
                    }

                    for (final Asn1SymbolDefinitionField symbolDefinitionField : symbolDefinition.getSymbolContent().getSymbolDefinitionFieldList()) {
                        if (symbolDefinitionField.getSymbolContent() != null) {
                            if (symbolDefinitionField.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN) != null &&
                                    symbolDefinitionField.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE) != null) {

                                final TextRange textRange = new TextRange(
                                        symbolDefinitionField.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                        symbolDefinitionField.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                                );
                                if (!textRange.isEmpty()) {
                                    list.add(new FoldingDescriptor(symbolDefinitionField.getSymbolContent(), textRange));
                                }
                            }
                        }
                    }
                }
            }
        }

        return list.toArray(new FoldingDescriptor[list.size()]);
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode astNode) {
        return false;
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode astNode) {
        return "...";
    }
}
