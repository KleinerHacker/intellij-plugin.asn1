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
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolContentVersion;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolValueSet;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolValueType;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;
import org.pcsoft.plugin.intellij.asn1.type.Asn1SymbolElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Christoph on 29.09.2016.
 */
public class Asn1ValueFolding extends FoldingBuilderEx {

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement psiElement, @NotNull Document document, boolean b) {
        final List<FoldingDescriptor> list = new ArrayList<>();

        final Collection<Asn1SymbolDefinition> symbolDefinitions = PsiTreeUtil.findChildrenOfType(psiElement, Asn1SymbolDefinition.class);
        for (final Asn1SymbolDefinition symbolDefinition : symbolDefinitions) {
            if (symbolDefinition.getSymbolElement() == Asn1SymbolElement.ValueDefinition && symbolDefinition.getSymbolTypeForValue() != null &&
                    symbolDefinition.getSymbolTypeForValue().getSymbolValue() != null && symbolDefinition.getSymbolTypeForValue().getSymbolValue().getSymbolValueType() != null) {
                final Asn1SymbolValueType symbolValueType = symbolDefinition.getSymbolTypeForValue().getSymbolValue().getSymbolValueType();

                final ASTNode curlyOpen = symbolValueType.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN);
                final ASTNode curlyClose = symbolValueType.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE);
                if (curlyOpen != null && curlyClose != null) {
                    final TextRange textRange = new TextRange(curlyOpen.getStartOffset() + 1, curlyClose.getStartOffset());
                    if (!textRange.isEmpty()) {
                        list.add(new FoldingDescriptor(symbolDefinition, textRange));
                    }
                }
            } else if ((symbolDefinition.getSymbolElement() == Asn1SymbolElement.ValueSetDefinition || symbolDefinition.getSymbolElement() == Asn1SymbolElement.ObjectSetDefinition ||
                    symbolDefinition.getSymbolElement() == Asn1SymbolElement.SetDefinition) && symbolDefinition.getSymbolTypeForSet() != null &&
                    symbolDefinition.getSymbolTypeForSet().getSymbolValueSet() != null) {
                final Asn1SymbolValueSet symbolValueSet = symbolDefinition.getSymbolTypeForSet().getSymbolValueSet();

                final ASTNode curlyOpen = symbolValueSet.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN);
                final ASTNode curlyClose = symbolValueSet.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE);
                if (curlyOpen != null && curlyClose != null) {
                    final TextRange textRange = new TextRange(curlyOpen.getStartOffset() + 1, curlyClose.getStartOffset());
                    if (!textRange.isEmpty()) {
                        list.add(new FoldingDescriptor(symbolDefinition, textRange));
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
        if (astNode.getPsi() instanceof Asn1SymbolContentVersion && ((Asn1SymbolContentVersion) astNode.getPsi()).getVersionNumber() != null) {
            return "Version " + ((Asn1SymbolContentVersion) astNode.getPsi()).getVersionNumber();
        }

        return "...";
    }
}
